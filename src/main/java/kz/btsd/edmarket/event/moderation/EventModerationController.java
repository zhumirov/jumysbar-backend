package kz.btsd.edmarket.event.moderation;

import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.elastic.EventElasticService;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventTitleDto;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.UserShortDto;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
public class EventModerationController {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventModerationRepository eventModerationRepository;
    @Autowired
    private EventElasticService eventElasticService;

    //при успешной модерации-APPROVED статус -  курс видетя все
    @PutMapping(value = "/events/moderate")
    public ResponseEntity<?> allByUserId(@RequestBody EventModeration eventModeration) {
        eventModeration = eventModerationRepository.save(eventModeration);
        if (eventModeration.getStatus().equals(ModerationStatus.APPROVED)) {
            Event event = eventRepository.findById(eventModeration.getEventId()).get();
            eventElasticService.publish(event);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/events/moderation")
    public List<ModerationRow> all(@RequestParam(defaultValue = "JUMYSBAR", required = false) Platform platform,
                                   @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                   @RequestParam(defaultValue = "asc", required = false) String order) {
        List<ModerationRow> table = new LinkedList<>();
        List<EventModeration> list =  eventModerationRepository.findByStatus(ModerationStatus.NEW, platform, SortUtils.buildSort(sort, order));
        for (EventModeration eventModeration :
                list) {
            UserShortDto user = userRepository.findByIdShortDto(eventModeration.getUserId()).get();
            EventTitleDto event = eventRepository.findByIdEventTitleDto(eventModeration.getEventId()).get();
            table.add(new ModerationRow(eventModeration, user, event));
        }
        return table;
    }


    @GetMapping(value = "/events/moderation/count")
    public long countAll(@RequestParam(defaultValue = "JUMYSBAR", required = false) Platform platform) {
        return eventModerationRepository.countByStatus(ModerationStatus.NEW, platform);
    }
}
