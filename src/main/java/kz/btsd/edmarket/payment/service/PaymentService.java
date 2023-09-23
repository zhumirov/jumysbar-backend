package kz.btsd.edmarket.payment.service;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.payment.model.Order;
import kz.btsd.edmarket.payment.model.Payment;
import kz.btsd.edmarket.subscription.model.SubscriptionCreateRequest;
import kz.btsd.edmarket.subscription.model.SubscriptionCreateResponse;
import kz.btsd.edmarket.subscription.repository.PaymentRepository;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Service
public class PaymentService {
    private RestTemplate restTemplate;
    @Value("${jumysbar.ecommerce24.url}")
    private String ecommercd24Url;
    @Value("${jumysbar.ecommerce24.username}")
    private String username;
    @Value("${jumysbar.ecommerce24.password}")
    private String password;
    @Value("${jumysbar.ecommerce24.return-url}")
    private String returnUrl;
    @Value("${jumysbar.ecommerce24.callback-url}")
    private String callbackUrl;
    @Value("${jumysbar.ecommerce24.demo}")
    private boolean demoEnabled;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @PostConstruct
    public void init() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        restTemplate = builder
                .basicAuthentication(username, password)
                .build();
    }

    public Payment create(User user, String description, String returnUrl, Order order) {
        Payment payment = new Payment();
        payment.setMerchantId(username);
        payment.setAmount(order.getPrice() * 100);
        payment.setOrderId(order.getId());//todo
        if (returnUrl != null) {
            payment.setReturnUrl(returnUrl + "?transactionId=" + order.getId());
        } else {
            payment.setReturnUrl(this.returnUrl);
        }
        payment.setCallbackUrl(callbackUrl);
        payment.setDescription(description);
        payment.setDemo(demoEnabled);
        payment.setCustomerData(new Payment.CustomerData(user.getEmail(), user.getPhone()));
        return payment;
    }

    public SubscriptionCreateResponse pay(Payment payment) {
        SubscriptionCreateResponse response = restTemplate.postForObject(ecommercd24Url, payment, SubscriptionCreateResponse.class);
        payment.setId(response.getId());
        payment.setUrl(response.getUrl());
        paymentRepository.save(payment);
        return response;
    }

    public SubscriptionCreateResponse pay(SubscriptionCreateRequest request, Order order) {
        User user = userRepository.findById(request.getUserId()).get(); //todo переделать
        Event event = eventRepository.findById(request.getEventId()).get(); //todo переделать
        String description = "Вы производите оплату курса: " + event.getTitle();
        Payment payment = create(user, description, request.getReturnUrl(), order);
        return pay(payment);
    }
}
