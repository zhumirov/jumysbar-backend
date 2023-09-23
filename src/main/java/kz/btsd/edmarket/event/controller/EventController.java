package kz.btsd.edmarket.event.controller;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.elastic.EventElasticService;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventResponse;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.model.EventStatusRequest;
import kz.btsd.edmarket.event.model.EventStatusResponse;
import kz.btsd.edmarket.event.model.EventTitleDto;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventBasketService;
import kz.btsd.edmarket.event.service.EventPublishService;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.online.module.service.ModuleService;
import kz.btsd.edmarket.online.progress.EventProgress;
import kz.btsd.edmarket.online.progress.EventProgressService;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.service.AuthService;
import kz.btsd.edmarket.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
public class EventController {
    @Autowired
    private EventRepository repository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private EventElasticService eventElasticService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProgressService eventProgressService;
    @Autowired
    private EventBasketService eventBasketService;
    @Autowired
    private EventPublishService eventPublishService;
    @Autowired
    private AuthService authService;

    @GetMapping("/events/{id}")
    public EventResponse findById(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @GetMapping("/events/{id}/online/{userId}")
    public EventResponse findByIdForUser(Authentication authentication, @PathVariable Long id, @PathVariable Long userId) {
        authService.checkOwner(authentication.getName(), userId);
        EventResponse eventResponse = findById(id);
        eventResponse.setSigned(subscriptionRepository.findByUserIdAndEventId(userId, id).isPresent());
        EventProgress eventProgress = eventProgressService.getOrCreateEventProgress(id, userId);
        eventResponse.setLastStepId(eventProgress.getLastStepId());
        moduleService.fillModuleFullDtos(eventResponse.getModules(), userId, eventProgress);
        return eventResponse;
    }

    @PostMapping("/events")
    public Event save(Authentication authentication, @Valid @RequestBody Event newEvent) {
        authService.checkOwner(authentication.getName(), newEvent.getUserId());

        if (!newEvent.getStatus().equals(EventStatus.DRAFT)) {
            throw new IllegalStateException("версия не может быть изменена");
        }
        eventService.fillEventDate(newEvent);
        Event event = repository.save(newEvent); //todo переделать на события
        eventElasticService.publish(event);
        return event;
    }

    @PutMapping("/events/{id}")
    public Event changeEvent(Authentication authentication, @Valid @RequestBody Event event, @PathVariable Long id) {
//        authService.checkOwner(authentication.getName(), event.getUserId());
        if (!event.getStatus().equals(EventStatus.DRAFT)) {
            throw new IllegalStateException("версия не может быть изменена");
        }
        eventService.fillEventDate(event);
        event = repository.save(event);
        eventElasticService.publish(event);
        return event;
    }

    @PutMapping("/events/{id}/publish")
    public EventStatusResponse publishEvent(Authentication authentication, @PathVariable Long id) {
        eventService.checkEventOwner(authentication.getName(), id);
        User owner = userService.findById(authentication.getName());

        return eventPublishService.publishEvent(id, owner);
    }

    @PutMapping("/events/{id}/unpublish")
    public ResponseEntity<?> unpublishEvent(Authentication authentication, @PathVariable Long id) {
        eventService.checkEventOwner(authentication.getName(), id);
        eventPublishService.unpublishEvent(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/events/{id}/clone")
    public EventTitleDto cloneEvent(Authentication authentication, @PathVariable Long id) {
        eventService.checkEventOwner(authentication.getName(), id);
        Event event = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        if (event.getStatus().equals(EventStatus.DRAFT)) {
            return eventService.cloneWithOutId(id);
        } else {
            throw new IllegalStateException("можно клонировать только DRAFT версию");
        }
    }


    @GetMapping("/events/{id}/delete")
    public int deleteTEst(Authentication authentication, @PathVariable Long id) {
        eventService.checkEventOwner(authentication.getName(), id);
        eventElasticService.publishDelete(id);
        return 1;
    }

    @PutMapping("/events/{id}/status")
    public EventStatusResponse changeStatus(Authentication authentication, @RequestBody EventStatusRequest request, @PathVariable Long id) {
        User user = userService.findById(authentication.getName());
        Event event = repository.findById(request.getEventId())
                .orElseThrow(() -> new EntityNotFoundException(id));
        if (!user.isOperator() && EventStatus.BLOCKED.equals(request.getStatus())) {
            throw new AuthorizationServiceException("только OPERATOR может изменить");
        }
        event.setStatus(request.getStatus());
        event.setReason(request.getReason());
        event = repository.save(event);
        eventElasticService.publish(event);
        return new EventStatusResponse(true);
    }

    @PutMapping("/events/{id}/restore")
    public ResponseEntity<?> restoreEvent(Authentication authentication, @PathVariable Long id) {
        eventService.checkEventOwner(authentication.getName(), id);
        eventBasketService.restoreEvent(id);
        return ResponseEntity.ok().build();
    }

    //todo продумать со статусами?
    @DeleteMapping("/events/{id}")
    public void delete(Authentication authentication, @PathVariable Long id) {
        eventService.checkEventOwner(authentication.getName(), id);
        eventBasketService.deleteEvent(id, EventStatus.DELETED);
    }

    @DeleteMapping("/events/full-delete/{id}")
    public void fullDelete(Authentication authentication, @PathVariable Long id) {
        eventService.checkEventOwner(authentication.getName(), id);
        eventBasketService.deleteEvent(id, EventStatus.FULL_DELETED);
    }
}
