package kz.btsd.edmarket.online.controller;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.online.model.Section;
import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.SubsectionDto;
import kz.btsd.edmarket.online.model.TestActionRequest;
import kz.btsd.edmarket.online.model.TestActionResponse;
import kz.btsd.edmarket.online.model.test.LessonTestActionRequest;
import kz.btsd.edmarket.online.model.test.LessonTestActionResponse;
import kz.btsd.edmarket.online.model.test.LessonTestStartRequest;
import kz.btsd.edmarket.online.model.test.StepTestActionRequest;
import kz.btsd.edmarket.online.model.test.StepTestAnswer;
import kz.btsd.edmarket.online.progress.EventProgress;
import kz.btsd.edmarket.online.progress.EventProgressRepository;
import kz.btsd.edmarket.online.progress.LessonProgress;
import kz.btsd.edmarket.online.progress.LessonProgressRepository;
import kz.btsd.edmarket.online.progress.LessonProgressService;
import kz.btsd.edmarket.online.repository.SectionRepository;
import kz.btsd.edmarket.online.service.SectionService;
import kz.btsd.edmarket.online.service.SubsectionService;
import kz.btsd.edmarket.user.mail.InboxReaderImap;
import kz.btsd.edmarket.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class TestController {
    @Autowired
    private SubsectionService subsectionService;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LessonProgressRepository lessonProgressRepository;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private EventProgressRepository eventProgressRepository;
    @Autowired
    private LessonProgressService lessonProgressService;
    @Autowired
    private AuthService authService;

    private final static long TIME_ADD = 2;

    @PostMapping("/tests/check")
    public TestActionResponse check(Authentication authentication, @Valid @RequestBody TestActionRequest testActionCheckRequest) {
        subsectionService.checkSubsectionOwner(authentication.getName(), testActionCheckRequest.getCardId());
        return new TestActionResponse(subsectionService.testCheck(testActionCheckRequest.getCardId(), testActionCheckRequest.getAnswers()));
    }

    @PostMapping("/tests/action")
    public TestActionResponse actionTest(Authentication authentication,  @Valid @RequestBody StepTestActionRequest request) {
        authService.checkOwner(authentication.getName(), request.getUserId());
        boolean rightAnswer = subsectionService.testCheck(request.getStepId(), request.getAnswers());
        if (rightAnswer) {
            Long eventId = eventRepository.findByStepId(request.getStepId()).getId();
            subsectionService.addToEventProgress(eventId, request.getUserId(), request.getStepId());
        }
        return new TestActionResponse(rightAnswer);
    }

    @PostMapping("/tests/lesson/start")
    public LessonProgress start(Authentication authentication, @Valid @RequestBody LessonTestStartRequest request, HttpServletRequest httpServletRequest) {
        authService.checkOwner(authentication.getName(), request.getUserId());
        Optional<LessonProgress> optionalLessonProgress = lessonProgressRepository.findByLessonIdAndUserId(request.getLessonId(), request.getUserId());
        LessonProgress lessonProgress;
        if (optionalLessonProgress.isPresent()) {
            lessonProgress = optionalLessonProgress.get();
        } else {
            Event event = eventRepository.findByLessonId(request.getLessonId());
            lessonProgress = new LessonProgress(request.getUserId(), event.getId(), request.getLessonId());
            lessonProgress.setSubsections(lessonProgressService.generateExamQuestions(request.getLessonId()));
            lessonProgress.setClientIP(httpServletRequest.getRemoteAddr());
            lessonProgress = lessonProgressRepository.save(lessonProgress);
        }
        return lessonProgress;
    }

    //удаление информации о прохождение экзамена
    @PostMapping("/tests/lesson/clear")
    public ResponseEntity<?> clear(Authentication authentication, @Valid @RequestBody LessonTestStartRequest request) {
        sectionService.checkSectionOwner(authentication.getName(), request.getLessonId());
        Optional<LessonProgress> optionalLessonProgress = lessonProgressRepository.findByLessonIdAndUserId(request.getLessonId(), request.getUserId());
        LessonProgress lessonProgress;
        if (optionalLessonProgress.isPresent()) {
            lessonProgress = optionalLessonProgress.get();
            Long eventId = lessonProgress.getEventId();
            lessonProgressRepository.delete(lessonProgress);
            SectionFullDto section = sectionService.findById(request.getLessonId());
            EventProgress eventProgress = eventProgressRepository.findByEventIdAndUserId(eventId, request.getUserId())
                    .orElseThrow(() -> new IllegalStateException("не подписанный пользователь"));
            eventProgress.getSections().remove(section.getId());
            for (SubsectionDto subsection :
                    section.getSubsections()) {
                eventProgress.getSubsections().remove(subsection.getId());
            }
            eventProgressRepository.save(eventProgress);
        }
        return ResponseEntity.ok().build();
    }

    public LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.of("Asia/Almaty"))
                .toLocalDateTime();
    }

    @PostMapping("/tests/lesson/finish")
    public LessonTestActionResponse finish(Authentication authentication, @Valid @RequestBody LessonTestActionRequest request) {
        Date finishedDate = new Date();
        LessonProgress lessonProgress = lessonProgressRepository.findById(request.getLessonProgressId())
                .orElseThrow(() -> new IllegalStateException("пользователь не начал тест"));
        if (lessonProgress.getFinishedDate() != null) {
            throw new IllegalStateException("пользователь уже выполнил тест");
        }
        authService.checkOwner(authentication.getName(), lessonProgress.getUserId());
        long testDuration = ChronoUnit.MINUTES.between(convertToLocalDateTime(lessonProgress.getCreatedDate()), convertToLocalDateTime(finishedDate));
        Section section = sectionRepository.findById(lessonProgress.getLessonId()).get();
        LessonTestActionResponse lessonTestActionResponse = new LessonTestActionResponse();
        if (section.getDuration() + TIME_ADD >= testDuration) {
            for (StepTestAnswer test : request.getStepTests()) {
                TestActionResponse testActionResponse = actionTest(authentication, new StepTestActionRequest(lessonProgress.getUserId(), test.getStepId(), test.getAnswers()));
                testActionResponse.setStepId(test.getStepId());
                lessonTestActionResponse.getStepTestResults().add(testActionResponse);
            }
            lessonProgress.setFinishedDate(finishedDate);
            lessonProgress.setAnswers(request.getStepTests());
            lessonProgress = lessonProgressRepository.save(lessonProgress);
            lessonTestActionResponse.setLessonProgress(lessonProgress);
            subsectionService.addTestToEventProgress(lessonProgress.getEventId(), lessonProgress.getUserId(), lessonProgress.getLessonId());
        } else {
            lessonProgress.setFinishedDate(finishedDate);
            lessonProgressRepository.save(lessonProgress);
            subsectionService.addTestToEventProgress(lessonProgress.getEventId(), lessonProgress.getUserId(), lessonProgress.getLessonId());
            throw new IllegalStateException("превышено время, результат не засчитан");
        }
        return lessonTestActionResponse;
    }

}
