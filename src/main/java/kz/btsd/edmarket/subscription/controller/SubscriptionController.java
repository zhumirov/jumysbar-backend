package kz.btsd.edmarket.subscription.controller;

import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.invitation.service.DeleteSubscription;
import kz.btsd.edmarket.invitation.service.DeleteSuscriptionEvent;
import kz.btsd.edmarket.invitation.service.InvitationService;
import kz.btsd.edmarket.notification.service.SubscriptionAddedEvent;
import kz.btsd.edmarket.payment.model.Order;
import kz.btsd.edmarket.payment.model.PaymentResult;
import kz.btsd.edmarket.payment.model.PaymentResultResponse;
import kz.btsd.edmarket.subscription.model.Subscription;
import kz.btsd.edmarket.subscription.model.SubscriptionCheckGetRequest;
import kz.btsd.edmarket.subscription.model.SubscriptionCheckResponse;
import kz.btsd.edmarket.subscription.model.SubscriptionCreateRequest;
import kz.btsd.edmarket.subscription.model.SubscriptionCreateResponse;
import kz.btsd.edmarket.subscription.model.SubscriptionCreatedKassaEvent;
import kz.btsd.edmarket.subscription.repository.OrderRepository;
import kz.btsd.edmarket.subscription.repository.PaymentResultRepository;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.subscription.service.SubscriptionService;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.AuthService;
import kz.btsd.edmarket.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class SubscriptionController {
    @Autowired
    private SubscriptionRepository repository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentResultRepository paymentResultRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private UserService userService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private EventService eventService;
    @Autowired
    private AuthService authService;

    //todo перенести в пеймент сервис

    /**
     * прием callBack и ответ в случае успеха
     *
     * @param paymentResult
     * @return
     */
    @RequestMapping(value = "/subscriptions/result", method = {RequestMethod.GET, RequestMethod.POST})
    public PaymentResultResponse findById(@RequestBody PaymentResult paymentResult) {
        paymentResultRepository.save(paymentResult);
        Long orderId = paymentResult.getOrderId();
        Order order = orderRepository.findById(orderId).get();
        order.setPaymentResultId(paymentResult.getId());
        orderRepository.save(order);
        if (paymentResult.getStatus() == 1) {
            Subscription subscription = subscriptionService.createAndSaveSubscription(order.getEventId(), order.getUserId(), order.getPrice(), order.getPlanId());
            publisher.publishEvent(new SubscriptionCreatedKassaEvent(this, subscription));
        }
        return new PaymentResultResponse(true);
    }

    @PostMapping("/subscriptions/create")
    public SubscriptionCreateResponse createPay(Authentication authentication, @RequestBody SubscriptionCreateRequest request) {
        authService.checkOwner(authentication.getName(), request.getUserId());
        // User user = userRepository.findById(request.getUserId()).get();
        Optional<Subscription> subscriptionOptional = repository.findByUserIdAndEventId(request.getUserId(), request.getEventId());
        if (subscriptionOptional.isPresent()) {
            throw new RuntimeException("Уже подписан");
        }
        Event event = eventRepository.findById(request.getEventId()).get();
        if (request.getPlanId() == null && event.getPlans().size() > 0) {
            throw new IllegalStateException("Не выбран тариф");
        }
        SubscriptionCreateResponse response = subscriptionService.createSubscription(request);
        if (response.getStatus().equals(SubscriptionCreateResponse.SubscriptionStatus.CREATED)) {
            publisher.publishEvent(new SubscriptionAddedEvent(this, response.getSubscription().getUserId(), response.getSubscription()));
        }
        return response;
    }

    @RequestMapping(value = "/subscriptions/check", method = {RequestMethod.GET, RequestMethod.POST})
    public SubscriptionCheckResponse check(Authentication authentication, @RequestBody SubscriptionCheckGetRequest subscriptionCheckGetRequest) {
        authService.checkOwner(authentication.getName(), subscriptionCheckGetRequest.getUserId());
        User user = userRepository.findById(subscriptionCheckGetRequest.getUserId()).get();
        Optional<Subscription> subscriptionOptional = repository.findByPhoneAndEventId(user.getPhone(), subscriptionCheckGetRequest.getEventId());
        SubscriptionCheckResponse subscriptionCheckResponse = new SubscriptionCheckResponse();
        if (subscriptionOptional.isPresent()) {
            subscriptionCheckResponse.setPresent(true);
            subscriptionCheckResponse.setSubscription(subscriptionOptional.get());
        } else {
            subscriptionCheckResponse.setPresent(false);
        }
        return subscriptionCheckResponse;
    }

    @RequestMapping(value = "/subscriptions/get", method = {RequestMethod.GET, RequestMethod.POST})
    public Subscription get(Authentication authentication, @RequestBody SubscriptionCheckGetRequest subscriptionCheckGetRequest) {
        authService.checkOwner(authentication.getName(), subscriptionCheckGetRequest.getUserId());
        User user = userRepository.findById(subscriptionCheckGetRequest.getUserId()).get();
        return repository.findByPhoneAndEventId(user.getPhone(), subscriptionCheckGetRequest.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Could not find subscription"));
    }

    @GetMapping("/subscriptions/{id}")
    public Subscription findById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    @PostMapping("/subscriptions")
    public Subscription save(Authentication authentication, @RequestBody Subscription subscription) {
        authService.checkOwner(authentication.getName(), subscription.getUserId());
        //todo удалить сохранение setTitle.
        Event event = eventRepository.findById(subscription.getEventId())
                .orElseThrow(() -> new EntityNotFoundException(subscription.getEventId()));
        subscription.setTitle(event.getTitle());
        return repository.save(subscription);
    }

    @DeleteMapping("/subscriptions/{id}")
    void delete(Authentication authentication, @PathVariable Long id) {
        Subscription subscription = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        Event event = eventRepository.findById(subscription.getEventId()).get();
        User user = userService.findById(authentication.getName());
        User subscriber =userRepository.findById(subscription.getUserId()).get();
        if (user.getId().equals(event.getUserId()) || user.getId().equals(subscriber.getId())) {
            repository.deleteById(id);
            invitationService.deleteToEvent(subscriber, event.getId());
            publisher.publishEvent(new DeleteSuscriptionEvent(this, new DeleteSubscription(event.getTitle(), subscription.getName(), subscription.getEmail())));

        } else {
            throw new AuthorizationServiceException("только владелец курса или подписчик может удалить подписку");
        }
    }

    //todo просмотреть название, данный метод возврашает все заявки пользователя
    @GetMapping("/subscriptions/user")
    List<Subscription> allByUserId(@RequestParam Long createdUserId,
                                   @RequestParam(defaultValue = "0", required = false) Integer from,
                                   @RequestParam(defaultValue = "20", required = false) Integer size,
                                   @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                   @RequestParam(defaultValue = "asc", required = false) String order) {
        return repository.findAllByUserId(createdUserId, PageRequest.of(from, size, SortUtils.buildSort(sort, order)));
    }

    //todo просмотреть название, данный метод возврашает все заявки курсы организации
    @GetMapping("/subscriptions/organizer")
    public List<Subscription> allByOwnerId(@RequestParam Long ownerId,
                                           @RequestParam(defaultValue = "0", required = false) Integer from,
                                           @RequestParam(defaultValue = "20", required = false) Integer size,
                                           @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                           @RequestParam(defaultValue = "asc", required = false) String order) {
        return repository.findAllByOwnerId(ownerId, PageRequest.of(from, size, SortUtils.buildSort(sort, order)));
    }

    @GetMapping("/subscriptions")
    List<Subscription> allByEventId(@RequestParam Long eventId,
                                    @RequestParam(defaultValue = "0", required = false) Integer from,
                                    @RequestParam(defaultValue = "20", required = false) Integer size,
                                    @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                    @RequestParam(defaultValue = "asc", required = false) String order) {
        //todo временно для fronta
        Optional<Event> childrenEvent = eventRepository.findByParentId(eventId);
        if (childrenEvent.isPresent()) {
            eventId = childrenEvent.get().getId();
        }
        //todo временно для fronta
        return repository.findAllByEventId(eventId, PageRequest.of(from, size, SortUtils.buildSort(sort, order)));
    }

    @GetMapping(value = "/subscriptions/files")
    public ResponseEntity<byte[]> allByEventIdToFile(@RequestParam Long eventId,
                                                     @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                                     @RequestParam(defaultValue = "asc", required = false) String order) {
        List<Subscription> list = repository.findAllByEventId(eventId, SortUtils.buildSort(sort, order));
        ByteArrayOutputStream stream = subscriptionService.createCVS(list);
        String fileName = "subscription" + eventId + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentLength(stream.size()) //
                .body(stream.toByteArray());
    }
}
