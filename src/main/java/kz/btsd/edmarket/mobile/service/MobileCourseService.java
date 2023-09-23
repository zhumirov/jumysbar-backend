package kz.btsd.edmarket.mobile.service;

import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventConverter;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.mobile.model.*;
import kz.btsd.edmarket.mobile.utils.AitubeUtils;
import kz.btsd.edmarket.mobile.utils.HtmlStringUtils;
import kz.btsd.edmarket.online.module.model.Module;
import kz.btsd.edmarket.online.module.repository.ModuleRepository;
import kz.btsd.edmarket.online.repository.SectionRepository;
import kz.btsd.edmarket.online.repository.SubsectionRepository;
import kz.btsd.edmarket.review.service.ReviewService;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class MobileCourseService {

    private final EventConverter eventConverter;
    private final EventRepository eventRepository;
    private final ModuleRepository moduleRepository;
    private final ReviewService reviewService;
    private final SectionRepository sectionRepository;
    private final SubsectionRepository subsectionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public List<MinimalCourseDto> findUserCourses(Long userId) {
        return subscriptionRepository.findEventIdByUserId(userId)
                .stream()
                .map(eventRepository::findById)
                .flatMap(e -> e.map(Stream::of).orElseGet(Stream::empty))
                .map(eventConverter::convertToMinimalDto)
                .collect(Collectors.toList());
    }

    public List<MinimalCourseDto> findAll(Platform platform, Sort sort) {
        return eventRepository
                .findAllByStatus(EventStatus.APPROVED, platform, sort)
                .stream()
                .map(eventConverter::convertToMinimalDto).collect(Collectors.toList());
    }

    // todo remove hardcode
    public CourseDto findById(Long id, Long userId) {
        Event event = eventRepository.findById(id).get();
        if (!EventStatus.APPROVED.equals(event.getStatus())) {
            throw new EntityNotFoundException("Event not found");
        }

        final boolean hasSubscription = userId != null && subscriptionRepository.existsByEventIdAndUserId(id, userId);

        User author = userRepository.findById(event.getUserId()).get();
        AuthorDto authorDto = new AuthorDto()
                .setName(author.getName())
                .setAbout(event.getAuthorDescription())
                .setAvatarId(event.getAuthorAvatarId())
                .setVideoId(AitubeUtils.getAitubeId(event.getAuthorVideoUrl()));

        List<PlanDto> plans = event.getPlans().stream()
                .map(plan -> new PlanDto()
                        .setId(plan.getId())
                        .setTitle(plan.getTitle())
                        .setPrice(plan.getPrice())
                        .setOptions(plan.getDescription() != null ? Arrays.asList(plan.getDescription().split("\n")) : Collections.emptyList()))
                .collect(Collectors.toList());

        CourseDto courseDto = new CourseDto()
                .setId(id)
                .setAuthor(authorDto)
                .setTitle(HtmlStringUtils.removeHtmlTags(event.getTitle()))
                .setDescription(HtmlStringUtils.removeHtmlTags(event.getDescription()))
                .setHasSubscription(hasSubscription)
                .setUpdated(new Date())
                .setStudents(subscriptionRepository.countByEventId(id))
                .setPreviewLink(AitubeUtils.getAitubeId(event.getIntroVideoUrl()))
                .setLangs(Arrays.asList("Английский", "Казахский (скоро)"))
                .setAcquiredSkills(event.getAcquiredSkills())
                .setCourseAbout(event.getAboutCourse())
                .setRequirements(event.getRequirements())
                .setAuditory(event.getAuditory())
                .setPlans(plans)
                .setAboutCourse(event.getAboutCourse())
                .setCourseFits(event.getCourseFits())
                .setRatings(reviewService.getRatingsByEventId(id))
                .setReviews(reviewService.findAllByEventId(id, SortUtils.buildSort("createdDate", "asc")));

        List<Module> modules = moduleRepository.findIdsByEventIdOrderByPosition(id)
                .stream()
                .map(moduleRepository::findById)
                .map(Optional::get)
                .collect(Collectors.toList());

        List<ContentDto> content = new ArrayList<>();
        for (Module module : modules) {
            List<LessonPreviewDto> lessons = new ArrayList<>();

            for (Long sectionId : sectionRepository.findIdsByModuleIdOrderByPosition(module.getId())) {
                sectionRepository.findById(sectionId)
                        .ifPresent(section -> {
                            LessonPreviewDto lesson = new LessonPreviewDto()
                                    .setId(section.getId())
                                    .setTitle(section.getTitle())
                                    .setHasOpen(subsectionRepository.existsBySectionIdAndFree(sectionId, true));
                            if (hasSubscription) {
                                lesson.setHasOpen(true);
                                int completedSubsections = subsectionRepository
                                        .completedSubsectionsCount(sectionId, userId, id);
                                int totalSubsections = subsectionRepository.countAllBySectionId(sectionId);
                                if (totalSubsections != 0) {
                                    lesson.setProgress(completedSubsections * 100 / totalSubsections);
                                }
                            }
                            lessons.add(lesson);
                        });
            }
            if (lessons.isEmpty()) {
                continue;
            }
            content.add(new ContentDto()
                    .setId(module.getId())
                    .setTitle(module.getTitle())
                    .setLessons(lessons));
        }

        courseDto.setContent(content);

        return courseDto;
    }
}
