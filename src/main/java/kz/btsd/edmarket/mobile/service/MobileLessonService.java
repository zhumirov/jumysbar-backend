package kz.btsd.edmarket.mobile.service;

import kz.btsd.edmarket.comment.like.repository.StepLikeRepository;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.file.model.FileDto;
import kz.btsd.edmarket.file.repository.FileRepository;
import kz.btsd.edmarket.mobile.model.OptionDto;
import kz.btsd.edmarket.mobile.model.LessonDto;
import kz.btsd.edmarket.mobile.model.SlideDto;
import kz.btsd.edmarket.mobile.model.enums.SlideType;
import kz.btsd.edmarket.mobile.utils.AitubeUtils;
import kz.btsd.edmarket.mobile.utils.HtmlStringUtils;
import kz.btsd.edmarket.online.model.*;
import kz.btsd.edmarket.online.module.model.Module;
import kz.btsd.edmarket.online.module.repository.ModuleRepository;
import kz.btsd.edmarket.online.progress.EventProgressRepository;
import kz.btsd.edmarket.online.repository.SectionRepository;
import kz.btsd.edmarket.online.repository.SubsectionRepository;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class MobileLessonService {

    private final FileRepository fileRepository;
    private final EventProgressRepository eventProgressRepository;
    private final ModuleRepository moduleRepository;
    private final SectionRepository sectionRepository;
    private final StepLikeRepository stepLikeRepository;
    private final SubsectionRepository subsectionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    public LessonDto getLesson(Long id, String phone) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));

        Module module = moduleRepository.findById(section.getModuleId())
                .orElseThrow(() -> new EntityNotFoundException("Module not found"));

        boolean hasSubscription = false;
        Set<Long> completedSlidesIds = new HashSet<>();

        Long userId = null;
        if (StringUtils.isNotBlank(phone)) {
            userId = userService.findByPhone(phone).getId();
            if (subscriptionRepository.findByUserIdAndEventId(userId, module.getEventId()).isPresent()) {
                hasSubscription = true;
                eventProgressRepository.findByEventIdAndUserId(module.getEventId(), userId)
                        .ifPresent(eventProgress -> eventProgress.getSubsections()
                                .forEach(idStr -> completedSlidesIds.add(idStr)));
            }
        }

        List<Subsection> subsections = subsectionRepository.findBySectionIdOrderByPositionAsc(id);
        List<SlideDto> slides = new ArrayList<>();
        boolean hasOpenSlides = hasSubscription;

        int lastIdx = 0;

        for (Subsection subsection : subsections) {
            if (subsection.isFree()) {
                hasOpenSlides = true;
            }
            if (subsection.isFree() || hasSubscription) {
                slides.add(buildSlideDto(subsection, completedSlidesIds, userId, hasSubscription));
            } else {
                slides.add(buildClosedSlideDto(subsection));
            }
            if (slides.get(slides.size() - 1).isCompleted()) {
                lastIdx = slides.size() - 1;
            }
        }

        return LessonDto.builder()
                .id(id)
                .title(section.getTitle())
                .lastIdx(lastIdx)
                .hasSubscription(hasSubscription)
                .hasFreeSlides(hasOpenSlides)
                .description(section.getDescription())
                .slides(slides)
                .build();
    }

    private SlideDto buildClosedSlideDto(Subsection subsection) {
        return SlideDto.builder()
                .id(subsection.getId())
                .free(false)
                .title(subsection.getTitle())
                .type(SlideType.locked)
                .build();
    }

    private SlideDto buildSlideDto(Subsection subsection, Set<Long> completedSlidesIds,
                                   Long userId, boolean hasSubscription) {

        SlideDto.SlideDtoBuilder builder = SlideDto.builder()
                .id(subsection.getId())
                .completed(completedSlidesIds.contains(subsection.getId()))
                .free(hasSubscription || subsection.isFree())
                .title(subsection.getTitle());

        applyUnits(builder, subsection.getUnits());
        if (userId != null) {
            applyLike(builder, userId, subsection.getId());
        }

        return builder.build();
    }

    private void applyUnits(SlideDto.SlideDtoBuilder builder, List<Unit> units) {
        List<String> links = new ArrayList<>();
        List<FileDto> attachments = new ArrayList<>();
        boolean isText = true;
        for (Unit unit : units) {
            switch (unit.getType()) {
                // secondary elements
                case TEXT:
                    builder.text(unit.getValue());
                    break;
                case URL:
                    links.add(unit.getValue());
                    break;
                case FILE:
                    if (StringUtils.isNotBlank(unit.getValue())) {
                        attachments.add(fileRepository.findDtoById(unit.getValue()));
                    }
                    break;
                // main element
                case IMAGE:
                    isText = false;
                    builder.type(SlideType.image)
                            .image(fileRepository.findDtoById(unit.getValue()));
                    break;
                case VIDEO:
                    isText = false;
                    builder.type(SlideType.video)
                            .videoUrl(AitubeUtils.getAitubeId(unit.getValue()));
                    break;
                case TEST:
                    isText = false;
                    applyTest(builder, unit);
                    break;
                case HOMEWORK:
                    isText = false;
                    builder.type(SlideType.homework)
                            .homeWork(HtmlStringUtils.removeHtmlTags(unit.getValue()));
            }
        }
        if (isText) {
            builder.type(SlideType.text);
        }
        if (!links.isEmpty()) {
            builder.links(links);
        }
        if (!attachments.isEmpty()) {
            builder.attachments(attachments);
        }
    }

    private void applyLike(SlideDto.SlideDtoBuilder builder, Long userId, Long subsectionId) {
        stepLikeRepository.findByUserIdAndStepId(userId, subsectionId)
                .ifPresent(stepLike -> builder.like(stepLike.getValue()));
    }

    private void applyTest(SlideDto.SlideDtoBuilder builder, Unit unit) {
        Set<String> answers = new HashSet<>(unit.getAnswers());
        if (TestType.SINGLE.equals(unit.getTestType()) || TestType.MULTIPLE.equals(unit.getTestType())) {
            builder.type(SlideType.testMultiple);
        } else {
            builder.type(SlideType.testFree);
        }
        builder.question(unit.getValue());
        if (CollectionUtils.isNotEmpty(unit.getOptions())) {
            builder.options(unit.getOptions().stream()
                    .filter(Objects::nonNull)
                    .map(option -> new OptionDto(option.getValue(), option.getHint(),
                            answers.contains(option.getValue())))
                    .collect(Collectors.toList()));
        }
        if (TestType.FREE.equals(unit.getTestType()) && CollectionUtils.isNotEmpty(unit.getAnswers())) {
            builder.answer(unit.getAnswers().get(0));
        }
    }

}
