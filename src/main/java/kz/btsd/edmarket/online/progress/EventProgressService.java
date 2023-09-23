package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.certificate.repository.CertificateRepository;
import kz.btsd.edmarket.comment.model.Comment;
import kz.btsd.edmarket.comment.repository.CommentRepository;
import kz.btsd.edmarket.event.model.EventResponse;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.online.controller.UserLessonActivityDto;
import kz.btsd.edmarket.online.model.SectionConverter;
import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.SectionProgressDto;
import kz.btsd.edmarket.online.model.SectionType;
import kz.btsd.edmarket.online.model.SubsectionConverter;
import kz.btsd.edmarket.online.model.SubsectionDto;
import kz.btsd.edmarket.online.model.SubsectionProgressDto;
import kz.btsd.edmarket.online.model.Unit;
import kz.btsd.edmarket.online.model.UnitType;
import kz.btsd.edmarket.online.module.model.ModuleConverter;
import kz.btsd.edmarket.online.module.model.ModuleDto;
import kz.btsd.edmarket.online.module.model.ModuleProgressDto;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressUserRow;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressUsersTableDto;
import kz.btsd.edmarket.online.service.SubsectionService;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventProgressService {
    @Autowired
    private ModuleConverter moduleConverter;
    @Autowired
    private SectionConverter sectionConverter;
    @Autowired
    private SubsectionConverter subsectionConverter;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SubsectionService subsectionService;
    @Autowired
    private LessonProgressRepository lessonProgressRepository;
    @Autowired
    private EventProgressRepository eventProgressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventProgressConverter eventProgressConverter;
    @Autowired
    private EventService eventService;
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public EventProgress getOrCreateEventProgress(Long eventId, Long userId) {
        Optional<EventProgress> optional = eventProgressRepository.findByEventIdAndUserId(eventId, userId);
        EventProgress eventProgress;
        if (!optional.isPresent()) {
            eventProgress = new EventProgress();
            eventProgress.setUserId(userId);
            eventProgress.setEventId(eventId);
            eventProgress = eventProgressRepository.save(eventProgress);
        } else {
            eventProgress = optional.get();
        }
        return eventProgress;
    }

    public EventProgressUsersTableDto fillForCertificate(Long eventId, List<Long> userIds) {
        EventProgressUsersTableDto eventProgressUsersDto = new EventProgressUsersTableDto();
        eventProgressUsersDto.setEventResponse(eventService.findById(eventId));
        List<EventProgress> eventProgresses = eventProgressRepository.findByEventIdAndUserIdIn(eventId, userIds);
        eventProgressUsersDto.setEventProgresses(eventProgresses.stream().map(eventProgress -> {
            User user = userRepository.findById(eventProgress.getUserId()).get();
            return eventProgressConverter.convertToTableDto(eventProgress, user);
        }).collect(Collectors.toList()));
        fillEventProgressesHomeWorkLikes(eventProgressUsersDto);
        return eventProgressUsersDto;
    }

    public void fillEventProgressesHomeWorkLikes(EventProgressUsersTableDto eventProgressUsersDto) {
        EventOnlineHomeworkTest eventOnlineHomeworkTest = convertToHomeworkAndTest(eventProgressUsersDto.getEventResponse());
        for (EventProgressUserRow row :
                eventProgressUsersDto.getEventProgresses()) {
            fillEventProgressFullStatsDtoHomeWorkLikes(row, eventOnlineHomeworkTest);
            row.setTotalPercent(calculateResolvedPercent(eventProgressUsersDto.getEventResponse(), row));
        }
    }

    private int calculateP2p(Long stepId, Long userId) {
        int score = 0;
        UserLessonActivityDto userLessonActivityDto = subsectionService.checkP2p(stepId, userId);
        if (userLessonActivityDto.getLikes() >= 3) {
            score++;
        }
        if (userLessonActivityDto.getDislikes() >= 1) {
            score++;
        }
        if (userLessonActivityDto.getComments() >= 1) {
            score++;
        }
        return score;
    }

    //todo надо переделать , с привязкой к курсу, eventId
    private Map<Long, Comment> getComments(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdAndHomeworkIsTrue(userId);
        Map<Long, Comment> commentMap = new HashMap<>();
        for (Comment comment :
                comments) {
            commentMap.put(comment.getStepId(), comment);
        }
        return commentMap;
    }

    private Map<Long, Long> getLessonProgresses(Long userId, Long eventId) {
        List<LessonProgress> lessonProgresses = lessonProgressRepository.findByUserIdAndEventId(userId, eventId);
        Map<Long, Long> lessonProgressMap = new HashMap<>();
        for (LessonProgress lessonProgress :
                lessonProgresses) {
            lessonProgressMap.put(lessonProgress.getLessonId(), lessonProgress.getId());
        }
        return lessonProgressMap;
    }

    private void fillEventProgressFullStatsDtoHomeWorkLikes(EventProgressUserRow row, EventOnlineHomeworkTest eventOnlineHomeworkTest) {
        Set<Long> subsectionSet = row.getSubsections();
        Map<Long, Comment> commentMap = getComments(row.getUser().getId());
        Map<Long, Long> lessonProgressMap = getLessonProgresses(row.getUser().getId(), row.getEventId());
        for (SectionFullDto exam :
                eventOnlineHomeworkTest.getExams()) {
            SectionProgressDto sectionProgressDto = sectionConverter.convertToProgressDto(exam);
            sectionProgressDto.setLessonProgressId(lessonProgressMap.get(exam.getId()));
            for (SubsectionProgressDto subsectionProgressDto :
                    sectionProgressDto.getSubsections()) {
                sectionProgressDto.setTestSize(sectionProgressDto.getTestSize() + 1);
                if (subsectionSet.contains(subsectionProgressDto.getId())) {
                    sectionProgressDto.setResolvedTestSize(sectionProgressDto.getResolvedTestSize() + 1);
                }
            }
            long score = exam.getScore() != null ? exam.getScore() : 10;
            if (exam.getExamSize() != null) {
                sectionProgressDto.setTestSize(exam.getExamSize());
            }
            sectionProgressDto.setResolvedTestSize(sectionProgressDto.getResolvedTestSize() * score / sectionProgressDto.getTestSize());
            sectionProgressDto.setTestSize(score);
            row.getExams().add(sectionProgressDto);
            row.setTestSize(row.getTestSize() + sectionProgressDto.getTestSize());
            row.setResolvedTestSize(row.getResolvedTestSize() + sectionProgressDto.getResolvedTestSize());
        }
        for (SubsectionDto homework :
                eventOnlineHomeworkTest.getHomeworks()) {
            SubsectionProgressDto subsectionProgressDto = subsectionConverter.convertToProgressDto(homework);
            for (Unit unit : homework.getUnits()) {
                if (unit.getType().equals(UnitType.HOMEWORK)) {
                    subsectionProgressDto.setModuleId(eventOnlineHomeworkTest.getModules().get(subsectionProgressDto.getId()));
                    subsectionProgressDto.setHomeworkSize(subsectionProgressDto.getHomeworkSize() + 10);
                    if (subsectionSet.contains(subsectionProgressDto.getId())) {
                        int grade=10;
                        //todo сейчас 1 шаг одна домашка, передалать
                        Comment comment = commentMap.get(homework.getId());
                        if (comment != null) {
                            subsectionProgressDto.setCommentLikes(comment.getLikes());
                            subsectionProgressDto.setCommentDislikes(comment.getDislikes());
                            if (unit.isHomeworkGrade()) {
                                grade = comment.getGrade() == null ? 0 : comment.getGrade().intValue();
                            }
                        }
                        //когда нету дедлайна по домашней работе
                        if (unit.getEndDate() == null) {
                            subsectionProgressDto.setResolvedHomeworkSize(subsectionProgressDto.getResolvedHomeworkSize() + grade);
                        } else {
                            if (comment != null) {
                                if (comment.getCreatedDate().before(unit.getEndDate())) {
                                    subsectionProgressDto.setResolvedHomeworkSize(subsectionProgressDto.getResolvedHomeworkSize() + grade);
                                }
                            }
                        }
                    }
                    if (unit.isP2p()) {
                        subsectionProgressDto.setHomeworkSize(subsectionProgressDto.getHomeworkSize() + 3);
                        subsectionProgressDto.setResolvedHomeworkSize(subsectionProgressDto.getResolvedHomeworkSize() + calculateP2p(homework.getId(), row.getUser().getId()));
                    }
                    row.getHomeworks().add(subsectionProgressDto);
                    row.setHomeworkSize(row.getHomeworkSize() + subsectionProgressDto.getHomeworkSize());
                    row.setResolvedHomeworkSize(row.getResolvedHomeworkSize() + subsectionProgressDto.getResolvedHomeworkSize());
                    break;
                }
            }
        }
        row.setResolvedTotalResult(row.getResolvedHomeworkSize() + row.getResolvedTestSize());//todo передлать формат
        row.setTotalResult(row.getHomeworkSize() + row.getTestSize());
        certificateRepository.findByUserIdAndEventId(row.getUser().getId(), row.getEventId())
                .ifPresent(cert -> row.setCertificateId(cert.getId()));
        subscriptionRepository.findByUserIdAndEventId(row.getUser().getId(), row.getEventId())
                .ifPresent(subscription -> row.setSubscriptionId(subscription.getId()));
    }


    public EventOnline convert(EventResponse eventResponse) {
        Map<Long, SectionFullDto> sections = new HashMap<>();
        Map<Long, SubsectionDto> subsections = new HashMap<>();
        for (ModuleDto moduleDto : eventResponse.getModules()) {
            for (SectionFullDto sectionFullDto : moduleDto.getSections()) {
                sections.put(sectionFullDto.getId(), sectionFullDto);
                for (SubsectionDto subsectionDto : sectionFullDto.getSubsections()) {
                    subsections.put(subsectionDto.getId(), subsectionDto);
                }
            }
        }
        return new EventOnline(eventResponse.getModules(), sections, subsections);
    }

    public double calculateResolvedPercent(EventResponse eventResponse, EventProgressUserRow row) {
        int totalSize=0;
        int resolvedSize=0;
        for (ModuleDto moduleDto : eventResponse.getModules()) {
            for (SectionFullDto sectionFullDto : moduleDto.getSections()) {
                if (sectionFullDto.getType().equals(SectionType.EXAM)) {
                    if (sectionFullDto.getExamSize() != null && sectionFullDto.getExamSize() > 0) {
                        totalSize++;
                        if (row.getSections().contains(sectionFullDto.getId())) {
                            resolvedSize++;
                        }
                    }
                } else {
                    for (SubsectionDto subsectionDto : sectionFullDto.getSubsections()) {
                        totalSize++;
                        if (row.getSubsections().contains(subsectionDto.getId())) {
                            resolvedSize++;
                        }
                    }
                }
            }
        }
        return resolvedSize*100/totalSize;
    }

    public EventOnlineHomeworkTest convertToHomeworkAndTest(EventResponse eventResponse) {
        List<SectionFullDto> exams = new LinkedList<>();
        List<SubsectionDto> homeworks = new LinkedList<>();
        Map<Long, Long> modules = new HashMap<>();
        for (ModuleDto moduleDto : eventResponse.getModules()) {
            for (SectionFullDto sectionFullDto : moduleDto.getSections()) {
                if (sectionFullDto.getType().equals(SectionType.EXAM)) {
                    if (sectionFullDto.getExamSize() != null && sectionFullDto.getExamSize() > 0) {
                        exams.add(sectionFullDto);
                    }
                } else {
                    for (SubsectionDto subsectionDto : sectionFullDto.getSubsections()) {
                        for (Unit unit :
                                subsectionDto.getUnits()) {
                            if (unit.getType().equals(UnitType.HOMEWORK)) {
                                homeworks.add(subsectionDto);
                                break;
                            }
                        }
                        modules.put(subsectionDto.getId(), moduleDto.getId());
                    }
                }
            }
        }
        return new EventOnlineHomeworkTest(exams, homeworks, modules);
    }

    public void fillEventProgresses(EventProgressUsersDto eventProgressUsersDto) {
        EventOnline eventOnline = convert(eventProgressUsersDto.getEventResponse());
        for (EventProgressFullStatsDto eventProgressFullStatsDto :
                eventProgressUsersDto.getEventProgresses()) {
            fillEventProgressFullStatsDto(eventProgressFullStatsDto, eventOnline);
        }
    }

    private void fillEventProgressFullStatsDto(EventProgressFullStatsDto eventProgressFullStatsDto, EventOnline eventOnline) {
        Set<Long> subsectionSet = eventProgressFullStatsDto.getSubsections();
        eventProgressFullStatsDto.setModules(new ArrayList<>());
        for (ModuleDto moduleDto :
                eventOnline.getModules()) {
            ModuleProgressDto moduleProgressDto = moduleConverter.convertToProgressDto(moduleDto);
            for (SectionProgressDto sectionProgressDto :
                    moduleProgressDto.getSections()) {
                for (SubsectionProgressDto subsectionProgressDto :
                        sectionProgressDto.getSubsections()) {
                    SubsectionDto subsection = eventOnline.getSubsections().get(subsectionProgressDto.getId());
                    for (Unit unit :
                            subsection.getUnits()) {
                        if (unit.getType().equals(UnitType.TEST)) {
                            subsectionProgressDto.setTestSize(subsectionProgressDto.getTestSize() + 1);
                            if (subsectionSet.contains(subsectionProgressDto.getId())) {
                                subsectionProgressDto.setResolvedTestSize(subsectionProgressDto.getResolvedTestSize() + 1);
                            }
                        }
                        if (unit.getType().equals(UnitType.HOMEWORK)) {
                            subsectionProgressDto.setHomeworkSize(subsectionProgressDto.getHomeworkSize() + 1);
                            if (subsectionSet.contains(subsectionProgressDto.getId())) {
                                subsectionProgressDto.setResolvedHomeworkSize(subsectionProgressDto.getResolvedHomeworkSize() + 1);
                            }
                        }
                    }
                    sectionProgressDto.setTestSize(sectionProgressDto.getTestSize() + subsectionProgressDto.getTestSize());
                    sectionProgressDto.setResolvedTestSize(sectionProgressDto.getResolvedTestSize() + subsectionProgressDto.getResolvedTestSize());
                    sectionProgressDto.setHomeworkSize(sectionProgressDto.getHomeworkSize() + subsectionProgressDto.getHomeworkSize());
                    sectionProgressDto.setResolvedHomeworkSize(sectionProgressDto.getResolvedHomeworkSize() + subsectionProgressDto.getResolvedHomeworkSize());
                }
                moduleProgressDto.setTestSize(moduleProgressDto.getTestSize() + sectionProgressDto.getTestSize());
                moduleProgressDto.setResolvedTestSize(moduleProgressDto.getResolvedTestSize() + sectionProgressDto.getResolvedTestSize());
                moduleProgressDto.setHomeworkSize(moduleProgressDto.getHomeworkSize() + sectionProgressDto.getHomeworkSize());
                moduleProgressDto.setResolvedHomeworkSize(moduleProgressDto.getResolvedHomeworkSize() + sectionProgressDto.getResolvedHomeworkSize());
            }
            eventProgressFullStatsDto.getModules().add(moduleProgressDto);
            eventProgressFullStatsDto.setTestSize(eventProgressFullStatsDto.getTestSize() + moduleProgressDto.getTestSize());
            eventProgressFullStatsDto.setResolvedTestSize(eventProgressFullStatsDto.getResolvedTestSize() + moduleProgressDto.getResolvedTestSize());
            eventProgressFullStatsDto.setHomeworkSize(eventProgressFullStatsDto.getHomeworkSize() + moduleProgressDto.getHomeworkSize());
            eventProgressFullStatsDto.setResolvedHomeworkSize(eventProgressFullStatsDto.getResolvedHomeworkSize() + moduleProgressDto.getResolvedHomeworkSize());

        }
    }
}
