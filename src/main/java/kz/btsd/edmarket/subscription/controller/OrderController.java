package kz.btsd.edmarket.subscription.controller;

import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.subscription.model.OrderEventRequest;
import kz.btsd.edmarket.subscription.model.OrderOwnerRequest;
import kz.btsd.edmarket.subscription.repository.OrderRepository;
import kz.btsd.edmarket.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private EventService eventService;

    //общая сумма продажи для организатора
    @RequestMapping(value = "/orders/amount/owner", method = {RequestMethod.GET, RequestMethod.POST})
    public Long amount(Authentication authentication, @Valid @RequestBody OrderOwnerRequest orderOwnerRequest) {
        authService.checkOwner(authentication.getName(), orderOwnerRequest.getUserId());
        return orderRepository.sumByUserId(orderOwnerRequest.getUserId());
    }

    //общая сумма продажи курса
    @RequestMapping(value = "/orders/amount/event", method = {RequestMethod.GET, RequestMethod.POST})
    public Long amount(Authentication authentication, @Valid @RequestBody OrderEventRequest orderEventRequest) {
        eventService.checkEventOwner(authentication.getName(), orderEventRequest.getEventId());
        return orderRepository.sumByEventId(orderEventRequest.getEventId());
    }
}
