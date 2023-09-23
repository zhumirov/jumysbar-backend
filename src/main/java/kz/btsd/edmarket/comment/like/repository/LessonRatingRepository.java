package kz.btsd.edmarket.comment.like.repository;

import kz.btsd.edmarket.comment.like.model.CellLessonRatingValueDto;
import kz.btsd.edmarket.comment.like.model.CommonLessonRatingValueDto;
import kz.btsd.edmarket.comment.like.model.LessonRating;
import kz.btsd.edmarket.comment.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface LessonRatingRepository extends CrudRepository<LessonRating, Long> {
    List<LessonRating> findByCreatedDateAfter(Date createdDate, Pageable pageable);
    long countByCreatedDateAfter(Date createdDate);

    Optional<LessonRating> findByUserIdAndLessonId(Long userId, Long lessonId);

    @Query("select new kz.btsd.edmarket.comment.like.model.CommonLessonRatingValueDto(avg(lr.useful), avg(lr.interest), count(lr.useful), count(lr.interest)) from LessonRating lr left join Section s on lr.lessonId = s.id left join Module m on s.moduleId = m.id " +
            "where m.eventId=:eventId")
    CommonLessonRatingValueDto avgCommonLessonRatingByEventId(@Param("eventId") Long eventId);

    @Query("select new kz.btsd.edmarket.comment.like.model.CellLessonRatingValueDto(avg(lr.useful), avg(lr.interest), count(lr.useful), count(lr.interest)) from LessonRating lr " +
            "where lr.lessonId=:lessonId")
    CellLessonRatingValueDto avgLessonRatingByLessonId(@Param("lessonId") Long lessonId);
}
