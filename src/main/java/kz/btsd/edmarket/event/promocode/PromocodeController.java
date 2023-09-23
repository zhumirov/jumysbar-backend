package kz.btsd.edmarket.event.promocode;

import kz.btsd.edmarket.event.model.EntityStatus;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.Plan;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class PromocodeController {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PromocodeService promocodeService;
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    @Autowired
    private SubscriptionService subscriptionService;

    @RequestMapping(value = "/promocode/check", method = {RequestMethod.GET, RequestMethod.POST})
    public PromocodeCheckResponse resetCheck(@Valid @RequestBody PromocodeCheckRequest request) {
        Optional<Promocode> optionalPromocode = promoCodeRepository.findByEventIdAndTitleAndStatus(request.getEventId(), request.getTitle(), EntityStatus.NEW);
        if (optionalPromocode.isPresent()) {
            Event event = eventRepository.findById(request.getEventId()).get();
            if (request.getPlanId() == null && event.getPlans().size() > 0) {
                throw new IllegalStateException("Не выбран тариф");
            }
            Plan choosedPlan = subscriptionService.findById(event.getPlans(), request.getPlanId());
            Long price = promocodeService.price(choosedPlan.getPrice(), optionalPromocode.get());
            return new PromocodeCheckResponse(true, price);
        } else {
            return new PromocodeCheckResponse(false);
        }
    }

}
