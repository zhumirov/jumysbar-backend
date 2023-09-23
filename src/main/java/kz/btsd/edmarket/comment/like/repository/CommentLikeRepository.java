package kz.btsd.edmarket.comment.like.repository;

import kz.btsd.edmarket.comment.like.model.CommentLike;
import kz.btsd.edmarket.comment.like.model.LikeValue;
import kz.btsd.edmarket.event.model.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface CommentLikeRepository extends CrudRepository<CommentLike, Long> {
    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);
    @Transactional
    void deleteByCommentId(Long commentId);

    @Query("select count(cl) from CommentLike cl join Comment c on cl.commentId = c.id where c.stepId=:stepId and c.homework=true and cl.value=:value and cl.userId=:userId")
    long countHomeworkLikesByStepIdAndUserId(@Param("userId") Long userId, @Param("stepId") Long stepId, @Param("value") LikeValue value);
}
