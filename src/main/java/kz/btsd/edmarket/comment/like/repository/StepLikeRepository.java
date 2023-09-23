package kz.btsd.edmarket.comment.like.repository;

import kz.btsd.edmarket.comment.like.model.StepLike;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StepLikeRepository extends CrudRepository<StepLike, Long> {
    Optional<StepLike> findByUserIdAndStepId(Long userId, Long stepId);
}
