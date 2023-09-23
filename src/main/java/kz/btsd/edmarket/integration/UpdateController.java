package kz.btsd.edmarket.integration;

import kz.btsd.edmarket.comment.model.CommentConverter;
import kz.btsd.edmarket.comment.repository.CommentRepository;
import kz.btsd.edmarket.common.ExceptionLog;
import kz.btsd.edmarket.common.ExceptionLogRepository;
import kz.btsd.edmarket.elastic.EventElasticService;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.online.progress.EventProgress;
import kz.btsd.edmarket.online.progress.EventProgressRepository;
import kz.btsd.edmarket.online.repository.SubsectionRepository;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.AuthService;
import kz.btsd.edmarket.view.repository.SubsectionUniqueViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

import static kz.btsd.edmarket.integration.DatalakeController.CREATED_DATE;
import static kz.btsd.edmarket.integration.DatalakeController.SORT;

@CrossOrigin(origins = "*")
@RestController
public class UpdateController {
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventElasticService eventElasticService;
    @Autowired
    private CommentConverter commentConverter;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExceptionLogRepository exceptionLogRepository;
    @Autowired
    private EventProgressRepository eventProgressRepository;
    @Autowired
    private SubsectionUniqueViewRepository subsectionUniqueViewRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private AuthService authService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SubsectionRepository subsectionRepository;

    @GetMapping("/events/updateAll") //todo moi metod
    public long updateAll(Authentication authentication) {
        authService.checkAdmin(authentication.getName());
        long count = repository.countByStatus(EventStatus.APPROVED);
        int size = 100;
        eventElasticService.deleteAll();
        for (int i = 0; i < count / size + 1; i++) {
            List<Event> events = repository.findAllByStatus(EventStatus.APPROVED, PageRequest.of(i, size));
            for (Event event :
                    events) {
                User user = userRepository.findById(event.getUserId()).get();
                if (user.isUser()) {
                    if (eventService.checkUserModerationForUserRole(user, event)) {
                        eventElasticService.publish(event);
                    }
                } else {
                    eventElasticService.publish(event);
                }
            }
        }
        return count;
    }

    @GetMapping("/events/updateView") //todo moi metod
    public long updateAllerr(Authentication authentication) {
        //  authService.checkAdmin(authentication.getName());
        long count = eventProgressRepository.count();//
        int size = 100;
        for (int i = 0; i < count / size + 1; i++) {
            List<EventProgress> eventProgresses = eventProgressRepository.findAll(PageRequest.of(i, size));
            for (EventProgress ep :
                    eventProgresses) {
                ep.getSubsections().removeIf(subsectionId -> !subsectionRepository.existsById(subsectionId));
                ep.setViewedSubsectionsSize(ep.getSubsections().size());
                eventProgressRepository.save(ep);
            }
        }
        return count;
    }

    @GetMapping("/exceptionlogs")
    public DataLakeDto<ExceptionLog> exceptionLogs(Authentication authentication,
                                                   @RequestParam(defaultValue = CREATED_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date createdDate,
                                                   @RequestParam(defaultValue = "0", required = false) Integer page,
                                                   @RequestParam(defaultValue = "20", required = false) Integer size) {
        authService.checkAdmin(authentication.getName());
        List<ExceptionLog> exceptionLogs = exceptionLogRepository.findByCreatedDateAfter(createdDate, PageRequest.of(page, size, SORT));
        return new DataLakeDto<>(exceptionLogs, exceptionLogRepository.countByCreatedDateAfter(createdDate));
    }

    @GetMapping("/exceptionlogs/clear")
    public void exceptionLogs(Authentication authentication) {
            authService.checkAdmin(authentication.getName());
        exceptionLogRepository.deleteAll();
    }
}
