package kz.btsd.edmarket.integration;

import kz.btsd.edmarket.comment.like.model.LessonRating;
import kz.btsd.edmarket.comment.like.repository.LessonRatingRepository;
import kz.btsd.edmarket.comment.model.CommentConverter;
import kz.btsd.edmarket.comment.model.CommentDto;
import kz.btsd.edmarket.comment.repository.CommentRepository;
import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.online.progress.EventProgress;
import kz.btsd.edmarket.online.progress.EventProgressRepository;
import kz.btsd.edmarket.subscription.model.Subscription;
import kz.btsd.edmarket.subscription.repository.PaymentResultRepository;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.model.UserDto;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class DatalakeController {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private EventProgressRepository eventProgressRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private LessonRatingRepository lessonRatingRepository;
    @Autowired
    private CommentConverter commentConverter;
    @Autowired
    private PaymentResultRepository paymentResultRepository;
    @Value("${jumysbar.datalake.user.phone}")
    private String datalakeUserPhone;

    public final static String CREATED_DATE = "2021-01-01";
    public final static Sort SORT = SortUtils.buildSort("createdDate", "asc");

    private void checkAccess(Authentication authentication) {

        if (authentication == null) {
            throw new AccessDeniedException("Access denied");
        } else {
            User user = userRepository.findById(Long.valueOf(authentication.getName())).get();
            if (!user.getPhone().equals(datalakeUserPhone)) {
                throw new AccessDeniedException("Access denied");
            }
        }
    }

    @GetMapping(value = "/datalake/users")
    public DataLakeDto<UserDto> users(@RequestParam(defaultValue = CREATED_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date createdDate,
                                      @RequestParam(defaultValue = "0", required = false) Integer page,
                                      @RequestParam(defaultValue = "20", required = false) Integer size,
                                      Authentication authentication) {
        checkAccess(authentication);
        List<UserDto> users = userRepository.findByCreatedDateAfterAndDeletedFalse(createdDate, PageRequest.of(page, size, SORT))
                .stream()
                .map(userConverter::convertToDto)
                .collect(Collectors.toList());
        return new DataLakeDto<>(users, userRepository.countByCreatedDateAfterAndDeletedFalse(createdDate));
    }

    @GetMapping(value = "/datalake/events")
    public DataLakeDto<Event> events(@RequestParam(defaultValue = CREATED_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date createdDate,
                                     @RequestParam(defaultValue = "0", required = false) Integer page,
                                     @RequestParam(defaultValue = "20", required = false) Integer size,
                                     Authentication authentication) {
        checkAccess(authentication);
        return new DataLakeDto<>(eventRepository.findByCreatedDateAfter(createdDate, PageRequest.of(page, size, SORT)),
                eventRepository.countByCreatedDateAfter(createdDate));
    }

    @GetMapping(value = "/datalake/subscriptions")
    public DataLakeDto<Subscription> subscriptions(@RequestParam(defaultValue = CREATED_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date createdDate,
                                                   @RequestParam(defaultValue = "0", required = false) Integer page,
                                                   @RequestParam(defaultValue = "20", required = false) Integer size,
                                                   Authentication authentication) {
        checkAccess(authentication);
        return new DataLakeDto<>(subscriptionRepository.findByCreatedDateAfter(createdDate, PageRequest.of(page, size, SORT)),
                subscriptionRepository.countByCreatedDateAfter(createdDate));
    }

    @GetMapping(value = "/datalake/event-progress")
    public DataLakeDto<EventProgress> eventProgresses(@RequestParam(defaultValue = CREATED_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date createdDate,
                                                      @RequestParam(defaultValue = "0", required = false) Integer page,
                                                      @RequestParam(defaultValue = "20", required = false) Integer size,
                                                      Authentication authentication) {
        checkAccess(authentication);
        return new DataLakeDto<>(eventProgressRepository.findByCreatedDateAfter(createdDate, PageRequest.of(page, size, SORT)),
                eventProgressRepository.countByCreatedDateAfter(createdDate));
    }

    @GetMapping(value = "/datalake/lessons/rating")
    public DataLakeDto<LessonRating> lessonRatings(@RequestParam(defaultValue = CREATED_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date createdDate,
                                                   @RequestParam(defaultValue = "0", required = false) Integer page,
                                                   @RequestParam(defaultValue = "20", required = false) Integer size,
                                                   Authentication authentication) {
        checkAccess(authentication);
        return new DataLakeDto<>(lessonRatingRepository.findByCreatedDateAfter(createdDate, PageRequest.of(page, size, SORT)),
                lessonRatingRepository.countByCreatedDateAfter(createdDate));
    }

    @GetMapping(value = "/datalake/comments")
    public DataLakeDto<CommentDto> comments(@RequestParam(defaultValue = CREATED_DATE, required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date createdDate,
                                            @RequestParam(defaultValue = "0", required = false) Integer page,
                                            @RequestParam(defaultValue = "20", required = false) Integer size,
                                            Authentication authentication) {
        checkAccess(authentication);
        List<CommentDto> commentDtos = commentRepository.findByCreatedDateAfter(createdDate, PageRequest.of(page, size, SORT))
                .stream()
                .map(comment -> commentConverter.convertToDto(comment))
                .collect(Collectors.toList());
        return new DataLakeDto<>(commentDtos,
                commentRepository.countByCreatedDateAfter(createdDate));
    }
}
