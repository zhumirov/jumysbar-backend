package kz.btsd.edmarket.comment.like.controller;


import kz.btsd.edmarket.comment.like.model.CellLessonRatingValueDto;
import kz.btsd.edmarket.comment.like.model.CommonLessonRatingValueDto;
import kz.btsd.edmarket.comment.like.model.LessonRating;
import kz.btsd.edmarket.comment.like.model.LessonRatingValueDto;
import kz.btsd.edmarket.comment.like.repository.LessonRatingRepository;
import kz.btsd.edmarket.comment.like.service.LessonRatingService;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class LessonRatingController {
    @Autowired
    private LessonRatingRepository lessonRatingRepository;
    @Autowired
    private LessonRatingService lessonRatingService;
    @Autowired
    private AuthService authService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventService eventService;

    @GetMapping("/lessons/rating/{id}")
    public LessonRating findById(@PathVariable Long id) {
        return lessonRatingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    //средний рейтинг курса.
    @GetMapping("/lessons/rating/lessons-events/{eventId}")
    public CommonLessonRatingValueDto commonRatingyEventId(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        if (event.getStatus().equals(EventStatus.DRAFT)) {
            eventId = eventRepository.findByParentId(eventId).get().getId();
        }
        return lessonRatingRepository.avgCommonLessonRatingByEventId(eventId);
    }

    //таблица уроков с рейтингом
    @GetMapping("/lessons/rating/lessons-events-table/{eventId}")
    public List<CellLessonRatingValueDto> commonRatingyEventIdTable(@PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId).get();
        if (event.getStatus().equals(EventStatus.DRAFT)) {
            eventId = eventRepository.findByParentId(eventId).get().getId();
        }
        return lessonRatingService.lessonRaitingTable(eventId);
    }

    @PostMapping("/lessons/rating")
    public LessonRating save(Authentication authentication, @Valid @RequestBody LessonRatingValueDto lessonRatingValueDto) {
        authService.checkOwner(authentication.getName(), lessonRatingValueDto.getUserId());
        return lessonRatingService.save(lessonRatingValueDto, true);
    }

    @PutMapping("/lessons/rating/{id}")
    public LessonRating changeEvent(Authentication authentication, @RequestBody LessonRatingValueDto lessonRatingValueDto, @PathVariable Long id) {
        authService.checkOwner(authentication.getName(), lessonRatingValueDto.getUserId());
        return lessonRatingService.save(lessonRatingValueDto, true);
    }

//    @DeleteMapping("/lessons/rating/{id}")
//    void delete(@PathVariable Long id) {
//        lessonRatingService.deleteById(id);
//    }
}
