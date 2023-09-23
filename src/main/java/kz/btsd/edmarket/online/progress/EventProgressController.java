package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.online.model.SectionProgressDto;
import kz.btsd.edmarket.online.model.SubsectionProgressDto;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressLastStep;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressReview;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressUserRow;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressUsersTableDto;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class EventProgressController {
    @Autowired
    private EventProgressRepository eventProgressRepository;
    @Autowired
    private EventProgressConverter eventProgressConverter;
    @Autowired
    private EventService eventService;
    @Autowired
    private EventProgressService eventProgressService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    public static final DecimalFormat df = new DecimalFormat("#.#");


    // статистика пользователей курса
    @GetMapping(value = "/event-progress/event/users")
    public EventProgressUsersDto searchByEventId(@RequestParam Long eventId,
                                                 @RequestParam(defaultValue = "0", required = false) Integer page,
                                                 @RequestParam(defaultValue = "20", required = false) Integer size) {
        //todo временно для fronta
        Optional<Event> childrenEvent = eventRepository.findByParentId(eventId);
        if (childrenEvent.isPresent()) {
            eventId = childrenEvent.get().getId();
        }
        //todo временно для fronta

        EventProgressUsersDto eventProgressUsersDto = new EventProgressUsersDto();
        eventProgressUsersDto.setEventResponse(eventService.findById(eventId));
        List<EventProgress> eventProgresses = eventProgressRepository.findByEventIdSubscription(eventId, PageRequest.of(page, size));
        eventProgressUsersDto.setEventProgresses(eventProgresses.stream().map(eventProgress -> {
            User user = userRepository.findById(eventProgress.getUserId()).get();
            return eventProgressConverter.convertToFullDto(eventProgress, user);
        }).collect(Collectors.toList()));
        eventProgressService.fillEventProgresses(eventProgressUsersDto);
        return eventProgressUsersDto;
    }

    private void formatter(List<EventProgressUserRow> eventProgresses) {
        for (EventProgressUserRow row :
                eventProgresses) {
            row.setTestSize(Double.parseDouble(df.format(row.getTestSize())));
            row.setResolvedTestSize(Double.parseDouble(df.format(row.getResolvedTestSize())));
            row.setTotalResult(Double.parseDouble(df.format(row.getTotalResult())));
            row.setResolvedTotalResult(Double.parseDouble(df.format(row.getResolvedTotalResult())));
            row.setTotalPercent(Double.parseDouble(df.format(row.getTotalPercent())));
            for (SectionProgressDto section :
                    row.getExams()) {
                section.setTestSize(Double.parseDouble(df.format(section.getTestSize())));
                section.setResolvedTestSize(Double.parseDouble(df.format(section.getResolvedTestSize())));
            }
            for (SubsectionProgressDto subsection :
                    row.getHomeworks()) {
                subsection.setTestSize(Double.parseDouble(df.format(subsection.getTestSize())));
                subsection.setResolvedTestSize(Double.parseDouble(df.format(subsection.getResolvedTestSize())));
            }
        }
    }

    // статистика пользователей курса //todo new table
    @GetMapping(value = "/event-progress/tests-homework")
    public EventProgressUsersTableDto searchByEventIdWithLikesAndHomeworks(@RequestParam Long eventId,
                                                                           @RequestParam(defaultValue = "0", required = false) Integer page,
                                                                           @RequestParam(defaultValue = "20", required = false) Integer size) {
        //todo временно для fronta
        Optional<Event> childrenEvent = eventRepository.findByParentId(eventId);
        if (childrenEvent.isPresent()) {
            eventId = childrenEvent.get().getId();
        }
        //todo временно для fronta

        EventProgressUsersTableDto eventProgressUsersDto = new EventProgressUsersTableDto();
        eventProgressUsersDto.setEventResponse(eventService.findById(eventId));
        eventProgressUsersDto.setTotalHits(eventProgressRepository.countByEventIdSubscription(eventId));
        List<EventProgress> eventProgresses = eventProgressRepository.findByEventIdSubscription(eventId, PageRequest.of(page, size));

        eventProgressUsersDto.setEventProgresses(eventProgresses.stream().map(eventProgress -> {
            User user = userRepository.findById(eventProgress.getUserId()).get();
            return eventProgressConverter.convertToTableDto(eventProgress, user);
        }).collect(Collectors.toList()));
        eventProgressService.fillEventProgressesHomeWorkLikes(eventProgressUsersDto);
        formatter(eventProgressUsersDto.getEventProgresses());
        return eventProgressUsersDto;
    }

    @PutMapping(value = "/event-progress/review")
    public ResponseEntity<?> review(@RequestBody EventProgressReview review) {
        EventProgress eventProgress = eventProgressRepository.findByEventIdAndUserId(review.getEventId(), review.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("не создан прогресс по курсу " + review.getEventId()));
        eventProgress.setReview(review.getReview());
        eventProgressRepository.save(eventProgress);
        return ResponseEntity.ok().build();
    }

    // последний просмотренный урок
    @GetMapping(value = "/event-progress/last-step")
    public Long searchByEventIdWithLikesAndHomeworks(@RequestParam Long eventId,
                                                     @RequestParam Long userId) {
        EventProgress eventProgress = eventProgressRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException("не создан прогресс по курсу " + eventId));
        return eventProgress.getLastStepId();
    }

    @PutMapping(value = "/event-progress/last-step")
    public ResponseEntity<?> review(@RequestBody EventProgressLastStep lastStep) {
        eventProgressRepository.updateLastStepId(lastStep.getEventId(), lastStep.getUserId(), lastStep.getLastStepId());
        return ResponseEntity.ok().build();
    }


}
