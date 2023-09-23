package kz.btsd.edmarket.comment.like.service;

import kz.btsd.edmarket.online.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LessonRatingAsyncService {
    @Autowired
    private SectionRepository sectionRepository;

    //todo переделать на апдейт в один шаг через базу
    @Async
    public void update(Long lessonId) {
        sectionRepository.updateLessonRating(lessonId);
    }
}
