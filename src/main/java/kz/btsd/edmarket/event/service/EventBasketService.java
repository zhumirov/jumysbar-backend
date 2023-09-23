package kz.btsd.edmarket.event.service;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.elastic.EventElasticService;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventConverter;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.moderation.EventModeration;
import kz.btsd.edmarket.event.moderation.EventModerationRepository;
import kz.btsd.edmarket.event.moderation.ModerationStatus;
import kz.btsd.edmarket.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EventBasketService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventConverter eventConverter;
    @Autowired
    private EventModerationRepository eventModerationRepository;
    @Autowired
    private EventElasticService eventElasticService;

    private void deleteAndSaveEvent(Event event, EventStatus deletedStatus) {
        event.setStatus(deletedStatus);
        eventRepository.save(event);
        eventElasticService.publishDelete(event.getId());
    }

    private void deleteEventModeration(Event event) {
        if (event.isPublishVersion()) {
            Optional<EventModeration> optional = eventModerationRepository.findByEventId(event.getId());
            if (optional.isPresent()) {
                if (optional.get().getStatus().equals(ModerationStatus.NEW)) {
                    eventModerationRepository.delete(optional.get());
                }
            }
        }
    }

    public void deleteEvent(Long id, EventStatus deletedStatus) {
        Event event = eventRepository.findById(id).get();
        deleteEventModeration(event);
        deleteAndSaveEvent(event, deletedStatus);
        Optional<Event> childOptional = eventRepository.findByParentId(id);
        if (childOptional.isPresent()) {
            deleteAndSaveEvent(childOptional.get(), deletedStatus);
        }
        if (event.getParentId() != null) {
            Event parentEvent = eventRepository.findById(event.getParentId()).get();
            deleteAndSaveEvent(parentEvent, deletedStatus);
        }
    }

    private void restoreAndSaveEvent(Event event) {
        event.setStatus(EventStatus.DRAFT);
        eventRepository.save(event);
    }

    public void restoreEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        if (event.getStatus().equals(EventStatus.DELETED)) {
            restoreAndSaveEvent(event);
            Optional<Event> childEventOptional = eventRepository.findByParentId(id);
            if (childEventOptional.isPresent()) {
                Event childEvent = childEventOptional.get();
                restoreAndSaveEvent(childEvent);
            }
            if (event.getParentId() != null) {
                Event parentEvent = eventRepository.findById(event.getParentId()).get();
                restoreAndSaveEvent(parentEvent);
            }
        } else {
            throw new IllegalStateException("версия не удалена");
        }
    }
}
