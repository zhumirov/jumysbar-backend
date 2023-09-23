package kz.btsd.edmarket.online.progress;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends CrudRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByLessonIdAndUserId(Long lessonId, Long userId);

    List<LessonProgress> findByUserIdAndEventId(Long userId, Long eventId);
    List<LessonProgress> findByEventId(Long eventId);

}
