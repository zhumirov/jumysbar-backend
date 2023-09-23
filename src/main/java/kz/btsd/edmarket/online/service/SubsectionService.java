package kz.btsd.edmarket.online.service;

import kz.btsd.edmarket.certificate.model.CertificateRequest;
import kz.btsd.edmarket.certificate.model.CertificateSettings;
import kz.btsd.edmarket.certificate.model.CertificateSettingsResponse;
import kz.btsd.edmarket.certificate.service.CertificateService;
import kz.btsd.edmarket.certificate.service.CertificateSettingsService;
import kz.btsd.edmarket.comment.like.model.LikeValue;
import kz.btsd.edmarket.comment.like.model.StepLike;
import kz.btsd.edmarket.comment.like.repository.CommentLikeRepository;
import kz.btsd.edmarket.comment.like.repository.StepLikeRepository;
import kz.btsd.edmarket.comment.repository.CommentRepository;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.file.model.FileDto;
import kz.btsd.edmarket.file.repository.FileRepository;
import kz.btsd.edmarket.online.controller.UserLessonActivityDto;
import kz.btsd.edmarket.online.model.*;
import kz.btsd.edmarket.online.progress.EventProgress;
import kz.btsd.edmarket.online.progress.EventProgressRepository;
import kz.btsd.edmarket.online.repository.SectionRepository;
import kz.btsd.edmarket.online.repository.SubsectionRepository;
import kz.btsd.edmarket.view.model.SubsectionUniqueView;
import kz.btsd.edmarket.view.repository.SubsectionUniqueViewRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class SubsectionService {
    @Autowired
    private StepLikeRepository stepLikeRepository;
    @Autowired
    private SubsectionRepository subsectionRepository;
    @Autowired
    private SubsectionConverter subsectionConverter;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private EventProgressRepository eventProgressRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SubsectionUniqueViewRepository subsectionUniqueViewRepository;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CertificateService certificateService;
    @Autowired
    private CertificateSettingsService certificateSettingsService;

    public boolean hiddenHomework(long id) {
        Subsection subsection = subsectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        for (Unit unit : subsection.getUnits()) {
            if (unit.getType().equals(UnitType.HOMEWORK)) {
                return unit.isHiddenHomework();
            }
        }
        return false;
    }

    public SubsectionDto findById(Long id) {
        Subsection subsection = subsectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        SubsectionDto subsectionDto = subsectionConverter.convertToDto(subsection);
        fillFileNames(subsectionDto);
        return subsectionDto;
    }

    public SubsectionDto findById(Long id, Long userId) {
        SubsectionDto subsectionDto = findById(id);
        if (userId != null) {
            fillMyLike(subsectionDto, userId);
        }
        fillFileNames(subsectionDto);
        return subsectionDto;
    }

    public void fillFileNames(SubsectionDto subsectionDto) {
        if (subsectionDto.getUnits() != null) {
            for (Unit unit :
                    subsectionDto.getUnits()) {
                if (UnitType.isFile(unit.getType()) && Strings.isNotBlank(unit.getValue())) {
                    FileDto fileDto = fileRepository.findDtoById(unit.getValue());
                    if (fileDto == null) {
                        unit.setFileName("empty&=" + subsectionDto.getId());
                    } else {
                        unit.setFileName(fileDto.getFileName());
                    }
                }
            }
        }
    }

    public void fillMyLike(SubsectionDto subsectionDto, Long userId) {
        if (userId != null) {
            Optional<StepLike> optinal = stepLikeRepository.findByUserIdAndStepId(userId, subsectionDto.getId());
            optinal.ifPresent(subsectionDto::setCurrentUserStepLike);
        }
    }

    public boolean testCheck(Long subsectionId, List<String> answers) {
        Subsection subsection = subsectionRepository.findById(subsectionId)
                .orElseThrow(() -> new EntityNotFoundException(subsectionId));
        answers.sort(String::compareToIgnoreCase);
        for (Unit unit : subsection.getUnits()) {
            if (unit.getType().equals(UnitType.TEST)) {
                List<String> rightAnswers = unit.getAnswers();
                rightAnswers.sort(String::compareToIgnoreCase);
                if (answers.size() != rightAnswers.size()) {
                    return false;
                }
                for (int i = 0; i < rightAnswers.size(); i++) {
                    if (!rightAnswers.get(i).equalsIgnoreCase(answers.get(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void addToEventProgress(Long eventId, Long userId, Long subsectionId) {
        EventProgress eventProgress = eventProgressRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new IllegalStateException("не подписанный пользователь"));
        Section section = sectionRepository.findBySubsectionid(subsectionId);
        if (section.getType().equals(SectionType.EXAM)) {
            eventProgress.setViewedExamSubsectionsSize(eventProgress.getViewedExamSubsectionsSize() + 1);
        }
        eventProgress.getSubsections().add(subsectionId);
        eventProgress.setViewedSubsectionsSize(eventProgress.getSubsections().size());
        Event event = eventRepository.findById(eventProgress.getEventId()).orElseThrow(() -> new IllegalStateException("Не найден event"));
        if (event.getSubsectionsSize().equals(eventProgress.getViewedSubsectionsSize())) {
            eventProgress.setEndDate(LocalDateTime.now());
            certificateSettingsService.findByEvenId(eventId).orElseThrow(() -> new IllegalStateException("Не найден сертификат для данного курса"));
            certificateService.saveCertificate(CertificateRequest.builder()
                    .eventId(eventId)
                    .users(Arrays.asList(userId))
                    .build());
        }
        eventProgressRepository.save(eventProgress);
        Optional<SubsectionUniqueView> optionalUniqueView = subsectionUniqueViewRepository.findBySubsectionIdAndUserId(subsectionId, userId);
        if (!optionalUniqueView.isPresent()) {
            subsectionUniqueViewRepository.save(new SubsectionUniqueView(userId, subsectionId));
        }
    }

    public void addTestToEventProgress(Long eventId, Long userId, Long sectionId) {
        EventProgress eventProgress = eventProgressRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new IllegalStateException("не подписанный пользователь"));
        eventProgress.getSections().add(sectionId);
        eventProgressRepository.save(eventProgress);
    }

    public int calcSectionProgress(Long eventId, Long subsectionId, Long userId) {
        Optional<Subsection> subsectionOptional = subsectionRepository.findById(subsectionId);
        if (subsectionOptional.isPresent()) {
            Subsection subsection = subsectionOptional.get();
            int completedSubsections = subsectionRepository
                    .completedSubsectionsCount(subsection.getSectionId(), userId, eventId);
            int totalSubsections = subsectionRepository.countAllBySectionId(subsection.getSectionId());
            if (totalSubsections != 0) {
                return completedSubsections * 100 / totalSubsections;
            }
        }
        return 0;
    }

    public UserLessonActivityDto checkP2p(Long stepId, @PathVariable Long userId) {
        UserLessonActivityDto activityDto = new UserLessonActivityDto();
        activityDto.setLikes(commentLikeRepository.countHomeworkLikesByStepIdAndUserId(userId, stepId, LikeValue.LIKE));
        activityDto.setDislikes(commentLikeRepository.countHomeworkLikesByStepIdAndUserId(userId, stepId, LikeValue.DISLIKE));
        activityDto.setComments(commentRepository.countReplyHomeworkByStepIdAndUserId(userId, stepId));
        activityDto.setMyHomework(commentRepository.countByUserIdAndStepIdAndHomeworkIsTrue(userId, stepId));
        return activityDto;
    }

    public void checkSubsectionOwner(String userId, Long subsectionId) {
        Subsection subsection = subsectionRepository.findById(subsectionId).get();
        sectionService.checkSectionOwner(userId, subsection.getSectionId());
    }
}
