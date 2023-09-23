package kz.btsd.edmarket.subscription.service;

import kz.btsd.edmarket.event.model.EntityStatus;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.Plan;
import kz.btsd.edmarket.event.promocode.PromoCodeRepository;
import kz.btsd.edmarket.event.promocode.Promocode;
import kz.btsd.edmarket.event.promocode.PromocodeService;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.payment.model.Order;
import kz.btsd.edmarket.payment.service.PaymentService;
import kz.btsd.edmarket.subscription.model.Subscription;
import kz.btsd.edmarket.subscription.model.SubscriptionCreateRequest;
import kz.btsd.edmarket.subscription.model.SubscriptionCreateResponse;
import kz.btsd.edmarket.subscription.model.SubscriptionCreatedEvent;
import kz.btsd.edmarket.subscription.model.SubscriptionCreatedKassaEvent;
import kz.btsd.edmarket.subscription.repository.OrderRepository;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SubscriptionService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PromocodeService promocodeService;
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Value("${jumysbar.subscription.demo}")
    private boolean subscriptionDemo;

    public ByteArrayOutputStream createCVS(List<Subscription> subscriptionList) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (
                CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(byteArrayOutputStream), CSVFormat.DEFAULT
                        .withHeader("Имя", "Телефон", "Почта", "Дата создания", "Оплата"));
        ) {
            for (Subscription subscription : subscriptionList) {
                csvPrinter.printRecord(subscription.getName(), subscription.getPhone(), subscription.getEmail(), subscription.getCreatedDate(), subscription.getPrice());
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream;
    }

    public Subscription createAndSaveSubscription(Long eventId, Long userId, Long price, Long planId) {
        Subscription subscription = new Subscription();
        User user = userRepository.findById(userId).get();
        Event event = eventRepository.findById(eventId).get();
        subscription.setUserId(userId);
        subscription.setEventId(eventId);
        subscription.setTitle(event.getTitle());
        subscription.setCreatedDate(new Date());
        subscription.setEmail(user.getEmail());
        subscription.setName(user.getName());
        subscription.setPhone(user.getPhone());
        subscription.setPrice(price);
        subscription.setPlanId(planId);
        subscription = subscriptionRepository.save(subscription);
        publisher.publishEvent(new SubscriptionCreatedEvent(this, subscription));
        return subscription;
    }

    public SubscriptionCreateResponse createSubscriptionFullPromocode(SubscriptionCreateRequest request, Promocode promocode){
        SubscriptionCreateResponse subscriptionCreateResponse = new SubscriptionCreateResponse();
        subscriptionCreateResponse.setStatus(SubscriptionCreateResponse.SubscriptionStatus.CREATED);
        Subscription subscription = createAndSaveSubscription(request.getEventId(), request.getUserId(), 0L, request.getPlanId());
        subscription.setPromocodeId(promocode.getId());
        subscriptionRepository.save(subscription);
        subscriptionCreateResponse.setSubscription(subscription);
        publisher.publishEvent(new SubscriptionCreatedEvent(this, subscription));
        return subscriptionCreateResponse;
    }

    public Plan findById(Set<Plan> plans, Long planId) {
        for (Plan plan :
                plans) {
            if (plan.getId().equals(planId)) {
                return plan;
            }
        }
        return null;
    }

    @Async
    public void checkAndCreateSubscriptionAsync(SubscriptionCreateRequest request) {
        if (!subscriptionRepository.existsByEventIdAndUserId(request.getEventId(), request.getUserId()) && eventService.freeEvent(request.getEventId())) {
            createSubscription(request);
        }
    }

    public SubscriptionCreateResponse createSubscription(SubscriptionCreateRequest request){
        Event event = eventRepository.findById(request.getEventId()).get();
        Plan choosedPlan = findById(event.getPlans(), request.getPlanId());
        Long choosedPlanId = choosedPlan != null ? choosedPlan.getId() : null;
        Long choosedPrice = choosedPlan != null ? choosedPlan.getPrice() : 0;
        if ((request.getPlanId() == null || choosedPlan.getPrice() == 0) || subscriptionDemo) {
            SubscriptionCreateResponse subscriptionCreateResponse = new SubscriptionCreateResponse();
            subscriptionCreateResponse.setStatus(SubscriptionCreateResponse.SubscriptionStatus.CREATED);
            Subscription subscription = createAndSaveSubscription(request.getEventId(), request.getUserId(), choosedPrice, choosedPlanId);
            subscriptionCreateResponse.setSubscription(subscription);
            if (subscriptionDemo) {
                publisher.publishEvent(new SubscriptionCreatedKassaEvent(this, subscription));
            }
            return subscriptionCreateResponse;
        } else {
            Order order = new Order();
            order.setUserId(request.getUserId());
            order.setEventId(request.getEventId());
            order.setPlanId(request.getPlanId());
            if (request.getPromocodeTitle() != null) {
                Promocode promocode = promoCodeRepository.findByEventIdAndTitleAndStatus(request.getEventId(), request.getPromocodeTitle(), EntityStatus.NEW).get();
                Long priceWithPromoCode = promocodeService.price(choosedPlan.getPrice(), promocode);
                if (priceWithPromoCode == 0) { //todo 100% промокод
                    return createSubscriptionFullPromocode(request, promocode);
                }
                order.setPrice(priceWithPromoCode);
                order.setPromocodeId(promocode.getId());
                order.setDiscountType(promocode.getDiscountType());
                order.setDiscountAmount(promocode.getDiscountAmount());
            } else {
                order.setPrice(choosedPlan.getPrice());
            }
            order = orderRepository.save(order);
            SubscriptionCreateResponse subscriptionCreateResponse = paymentService.pay(request, order);
            subscriptionCreateResponse.setStatus(SubscriptionCreateResponse.SubscriptionStatus.PAY);
            order.setPaymentId(subscriptionCreateResponse.getId());
            orderRepository.save(order);
            return subscriptionCreateResponse;
        }
    }
}
