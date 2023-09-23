//package kz.btsd.edmarket.payment.service;
//
//import kz.btsd.edmarket.event.model.Event;
//import kz.btsd.edmarket.payment.model.Payment;
//import kz.btsd.edmarket.subscription.model.SubscriptionCreateResponse;
//import kz.btsd.edmarket.user.model.User;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class PaymentServiceTest {
//    @Autowired
//    private PaymentService paymentService;
//
////    @BeforeEach
////    void setUp() {
////    }
//
//    @Test
//    void create() {
//      //  Payment payment = paymentService.create(createUser(), createEvent(), null );
//        assertThat(!payment.getMerchantId().isEmpty()); //todo isblank
//    }
//
//    @Test
//    void pay() {
//        Payment payment = paymentService.create(createUser(), createEvent(), null);
//        SubscriptionCreateResponse response = paymentService.pay(payment);
//        System.out.println(response);
//        assertNotNull(response.getId());
//        assertThat(!response.getUrl().isEmpty()); //todo isblank
//    }
//
//    private Event createEvent(){
//        Event event = new Event();
//        event.setId(1l);
//        event.setTitle("Основы Java");
//        event.setPrice(1000l);
//        return event;
//    }
//
//    private User createUser(){
//        User user = new User();
//        user.setId(1000l);
//        user.setEmail("test@edmarket.kz");
//        user.setPhone("+7123456789");
//        return user;
//    }
//}
