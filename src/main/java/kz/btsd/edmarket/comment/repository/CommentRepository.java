package kz.btsd.edmarket.comment.repository;

import kz.btsd.edmarket.comment.model.Comment;
import kz.btsd.edmarket.notification.model.NotificationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByCreatedDateAfter(Date createdDate, Pageable pageable);

    Long countByCreatedDateAfter(Date createdDate);

    long countByStepIdAndHomeworkIsTrue(Long stepId);

    @Query("select count(c) from Comment c left join Comment ct on ct.id = c.threadId where c.stepId=:stepId and c.homework=false and (c.threadId is null or ct.homework=false)")
    long countCommentWithOutHomeworkTab(@Param("stepId") Long stepId);

    List<Comment> findByStepIdAndHomeworkAndThreadIdIsNull(Long stepId, boolean homework, Pageable pageable);

    List<Comment> findByStepIdAndHomeworkAndUserIdAndThreadIdIsNull(Long stepId, boolean homework, long userId, Pageable pageable);

    long countByStepIdAndHomeworkAndUserIdAndThreadIdIsNull(Long stepId, boolean homework, long userId);

    List<Comment> findByUserIdAndHomeworkAndThreadIdIsNull(Long userId, boolean homework, Pageable pageable);

    long countByUserIdAndStepIdAndHomeworkIsTrue(Long userId, Long stepId);

    List<Comment> findByStepIdAndThreadIdIsNullOrderByCreatedDate(Long stepId, Pageable pageable);

    List<Comment> findByLessonIdAndThreadIdIsNullOrderByCreatedDate(Long lessonId, Pageable pageable);

    @Query("select c from Comment c left join Section s on c.lessonId = s.id left join Module m on s.moduleId = m.id " +
            "where m.eventId=:eventId and c.threadId is null order by c.createdDate")
    List<Comment> findLessonsByEventIdOrderByCreatedDate(@Param("eventId") Long eventId, Pageable pageable);

    long countByLessonId(Long lessonId);

    @Query("select count(c) from Comment c left join Section s on c.lessonId = s.id left join Module m on s.moduleId = m.id " +
            "where m.eventId=:eventId and c.threadId is null")
    long countLessonsByEventId(@Param("eventId") Long eventId);

    List<Comment> findByThreadIdOrderByCreatedDate(Long threadId, Pageable pageable);

    List<Comment> findByUserIdAndThreadIdIsNullOrderByCreatedDate(Long userId, Pageable pageable);

    List<Comment> findByStepId(Long stepId);

    List<Comment> findByUserIdAndStepIdAndHomeworkIsTrue(Long userId, Long stepId);

    List<Comment> findByUserIdAndHomeworkIsTrue(Long userId);

    List<Comment> findByLessonId(Long lessonId);

    List<Comment> findAllByStepIdIsNullAndLessonIdIsNull();

    List<Comment> findAllByStepIdIsNullAndLessonIdIsNullAndThreadIdIsNotNull();

    long countByHomework(boolean homework);

    List<Comment> findAllByHomework(boolean homework, Pageable pageable);

    @Query("select count(s) from Comment s where s.userId = ?1 and s.stepId=?2 and s.homework=true")
    int countUserIdAndSteId(Long userId, Long stepId);

    @Transactional
    @Modifying
    @Query("UPDATE Comment c SET " +
            "c.likes = (select count(cl.id) from CommentLike cl where cl.commentId=?1 and cl.value=kz.btsd.edmarket.comment.like.model.LikeValue.LIKE), " +
            "c.dislikes = (select count(cl.id) from CommentLike cl where cl.commentId=?1 and cl.value=kz.btsd.edmarket.comment.like.model.LikeValue.DISLIKE) " +
            "WHERE c.id=?1")
    void updateCommentLikeAndDislike(Long commentId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE \"comment\" c SET " +
            "likes = (select count(cl.id) from Comment_Like cl where cl.comment_Id=?1 and cl.value='LIKE'), " +
            "dislikes = (select count(cl.id) from Comment_Like cl where cl.comment_Id=?1 and cl.value='DISLIKE') " +
            "WHERE c.id=?1", nativeQuery = true)
    void updateCommentLikeAndDislikeNative(Long commentId);

    long countByAnswerToId(Long answerToId);

    @Query("select count(c) from Comment c join Comment ct on ct.id = c.threadId where c.stepId=:stepId and ct.homework=true and c.homework=false and c.userId=:userId")
    long countReplyHomeworkByStepIdAndUserId(@Param("userId") Long userId, @Param("stepId") Long stepId);
}
