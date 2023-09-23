package kz.btsd.edmarket.event.controller.search;

import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.moderation.EventModeration;
import kz.btsd.edmarket.event.moderation.EventModerationRepository;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.user.model.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class EventSearchController {
    @Autowired
    private EventRepository repository;
    @Autowired
    private EventService eventService;
    @Autowired
    private EventModerationRepository eventModerationRepository;

    /**
     * список курсов где userId является ментором
     */
    @GetMapping(value = "/events/mentors")
    public List<Event> allByUserId(@RequestParam Long userId) {
        return repository.findMentorEventsByUserId(userId);
    }

    @GetMapping(value = "/events", params = "userId")
    public List<Event> allByUserId(@RequestParam Long userId,
                                   @RequestParam(defaultValue = "JUMYSBAR", required = false) Platform platform,
                                   @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                   @RequestParam(defaultValue = "false", required = false) boolean deleted,
                                   @RequestParam(defaultValue = "false", required = false) boolean otherUsers,
                                   @RequestParam(defaultValue = "asc", required = false) String order) {
        List<Event> events= eventService.findOrgEventByUserId(userId, platform, deleted, otherUsers, sort, order);
        for (Event event :
                events) {
            eventModerationRepository.findByEventId(event.getId()).ifPresent(event::setModeration);
        }
        return events;
    }
}
