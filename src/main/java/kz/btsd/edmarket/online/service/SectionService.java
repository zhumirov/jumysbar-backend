package kz.btsd.edmarket.online.service;

import kz.btsd.edmarket.comment.like.model.LessonRating;
import kz.btsd.edmarket.comment.like.repository.LessonRatingRepository;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.online.model.Section;
import kz.btsd.edmarket.online.model.SectionConverter;
import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.SectionType;
import kz.btsd.edmarket.online.model.Subsection;
import kz.btsd.edmarket.online.model.SubsectionDto;
import kz.btsd.edmarket.online.model.TestType;
import kz.btsd.edmarket.online.model.Unit;
import kz.btsd.edmarket.online.module.service.ModuleService;
import kz.btsd.edmarket.online.progress.EventProgress;
import kz.btsd.edmarket.online.progress.LessonProgressRepository;
import kz.btsd.edmarket.online.repository.SectionRepository;
import kz.btsd.edmarket.online.repository.SubsectionRepository;
import kz.btsd.edmarket.webinar.service.WebinarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SectionService {
    @Autowired
    private SubsectionRepository subsectionRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private SectionConverter sectionConverter;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LessonRatingRepository lessonRatingRepository;
    @Autowired
    private WebinarService webinarService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    public void fillSectionFullDto(List<SectionFullDto> list, EventProgress eventProgress) {
        Set<Long> viewedSubsections = eventProgress.getSubsections();
        for (SectionFullDto sectionFullDto : list) {
            int viewed = 0;
            for (SubsectionDto subsectionDto : sectionFullDto.getSubsections()) {
                if (viewedSubsections.contains(subsectionDto.getId())) {
                    subsectionDto.setViewed(true);
                    viewed++;
                }
            }
            //todo переделать setSubsectionsSize вынести на уровень сущности EventOnline
            sectionFullDto.setSubsectionsSize(sectionFullDto.getSubsections().size());
            sectionFullDto.setViewedSubsectionsSize(viewed);
            sectionFullDto.setResolvedTest(eventProgress.getSections().contains(sectionFullDto.getId()));
            if (sectionFullDto.getType().equals(SectionType.EXAM)) {
                lessonProgressRepository.findByLessonIdAndUserId(sectionFullDto.getId(), eventProgress.getUserId())
                        .ifPresent(lp -> sectionFullDto.setLessonProgressId(lp.getId()));
            }
        }
    }

    public List<SectionFullDto> findAllByEventId(Long id) {
        List<Long> ids = sectionRepository.findIdsByEventId(id);
        List<SectionFullDto> sections = new ArrayList<>();
        for (Long sectionId : ids) {
            sections.add(findById(sectionId));
        }
        sections.sort(Comparator.comparing(SectionFullDto::getPosition));
        return sections;
    }

    public Section findByIdWithLazy(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        section.setSubsections(subsectionRepository.findBySectionIdOrderByPositionAsc(section.getId()));
        return section;
    }

    public SectionFullDto findById(Long id) {
        Section section = sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        section.setSubsections(subsectionRepository.findBySectionIdOrderByPositionAsc(section.getId()));
        return sectionConverter.convertToFullDto(section);
    }

    public void calculateSection(Section section) {
        section.setSubsectionsSize(section.getSubsections().size());
        for (int i = 0; i < section.getSubsections().size(); i++) {
            section.getSubsections().get(i).setPosition((long) i);
        }
    }

    public boolean validTest(Unit unit) {
        if (unit.getTestType().equals(TestType.FREE)) {
            if (unit.getAnswers().size() == 0) {
                return false;
            }
        } else {
            if (unit.getAnswers().size() == 0 || unit.getOptions().size() == 0) {
                return false;
            }
        }
        return true;
    }

    public void removeEmptyUnit(Section section) {
        for (Subsection subsection :
                section.getSubsections()) {
            Iterator<Unit> iterator = subsection.getUnits().iterator();
            while (iterator.hasNext()) {
                Unit unit = iterator.next();
                switch (unit.getType()) {
                    case TEST: {
                        if (!validTest(unit)) {
                            iterator.remove();
                        }
                        break;
                    }
                    case WEBINAR: {
                        if (unit.getValue() == null || "".equals(unit.getValue())) {
                            int randomNum = ThreadLocalRandom.current().nextInt(10000, 100000);
                            String meetingId = "random-" + randomNum;
                            webinarService.createAndOpen(meetingId);
                            unit.setValue(meetingId);
                        }
                    }
                    default: {
                        if (unit.getValue() == null) {
                            iterator.remove();
                        }
                        break;
                    }
                }
            }
        }
    }

    public SectionFullDto save(SectionFullDto sectionFullDto) {
        Section section = sectionConverter.convertToEntity(sectionFullDto);
        removeEmptyUnit(section);
        calculateSection(section);
        section = sectionRepository.save(section);
        eventRepository.updateSubsectionsSize(section.getEventId()); //todo можно сделать асинхронно через события
        return findById(section.getId());
    }

    public void delete(Long id) {
        sectionRepository.deleteById(id);
    }

    public void fillMyLessonRating(SectionFullDto sectionFullDto, Long userId) {
        if (userId != null) {
            Optional<LessonRating> optinal = lessonRatingRepository.findByUserIdAndLessonId(userId, sectionFullDto.getId());
            if (optinal.isPresent()) {
                sectionFullDto.setCurrentUserLessonRating(optinal.get());
            }
        }
    }

    public void checkSectionOwner(String userId, Long sectionId) {
        Section section = sectionRepository.findById(sectionId).get();
        moduleService.checkModuleOwner(userId, section.getModuleId());
    }
}
