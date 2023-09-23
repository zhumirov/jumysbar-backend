package kz.btsd.edmarket.comment.like.service;

import kz.btsd.edmarket.comment.like.model.CellLessonRatingValueDto;
import kz.btsd.edmarket.comment.like.model.LessonRating;
import kz.btsd.edmarket.comment.like.model.LessonRatingType;
import kz.btsd.edmarket.comment.like.model.LessonRatingValueDto;
import kz.btsd.edmarket.comment.like.repository.LessonRatingRepository;
import kz.btsd.edmarket.comment.repository.CommentRepository;
import kz.btsd.edmarket.event.model.EventResponse;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.module.model.ModuleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LessonRatingService {
    @Autowired
    private LessonRatingRepository lessonRatingRepository;
    @Autowired
    private LessonRatingAsyncService lessonRatingAsyncService;
    @Autowired
    private EventService eventService;
    @Autowired
    private CommentRepository commentRepository;

    //todo переделать на апдейт в один шаг через базу
    public LessonRating save(LessonRatingValueDto request, boolean firstAttempt) {
        LessonRating lessonRating;
        Optional<LessonRating> optionaLessonRating = lessonRatingRepository.findByUserIdAndLessonId(request.getUserId(), request.getLessonId());
        if (optionaLessonRating.isPresent()) {
            lessonRating = optionaLessonRating.get();
        } else {
            lessonRating = new LessonRating(request.getUserId(), request.getLessonId());
        }
        if (request.getType().equals(LessonRatingType.INTEREST)) {
            lessonRating.setInterest(request.getValue());
        }
        if (request.getType().equals(LessonRatingType.USEFUL)) {
            lessonRating.setUseful(request.getValue());
        }
        try {
            lessonRating = lessonRatingRepository.save(lessonRating);
            lessonRatingAsyncService.update(lessonRating.getLessonId());
        } catch (DataIntegrityViolationException ex) {
            if (firstAttempt) {
                lessonRating = save(request, false);
            }
        }
        return lessonRating;
    }


    public List<CellLessonRatingValueDto> lessonRaitingTable(Long eventId) {
        EventResponse eventResponse = eventService.findById(eventId);
        List<CellLessonRatingValueDto> list = new ArrayList<>();
        for (ModuleDto moduleDto : eventResponse.getModules()) {
            for (SectionFullDto sectionFullDto : moduleDto.getSections()) {
                if (sectionFullDto.getReview()) {
                    CellLessonRatingValueDto lrValue = lessonRatingRepository.avgLessonRatingByLessonId(sectionFullDto.getId());
                    lrValue.setComments(commentRepository.countByLessonId(sectionFullDto.getId()));
                    lrValue.setLessonId(sectionFullDto.getId());
                    lrValue.setLessonTitle(sectionFullDto.getTitle());
                    list.add(lrValue);
                }
            }
        }
        return list;
    }
}
