package kz.btsd.edmarket.comment.like.service;

import kz.btsd.edmarket.comment.like.model.StepLike;
import kz.btsd.edmarket.comment.like.repository.StepLikeRepository;
import kz.btsd.edmarket.online.repository.SubsectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StepLikeService {
    @Autowired
    private StepLikeRepository stepLikeRepository;
    @Autowired
    private StepLikeAsyncService stepLikeAsyncService;

    //todo переделать на апдейт в один шаг через базу
    public StepLike save(StepLike stepLike, boolean firstAttempt) {
        Optional<StepLike> optionalOldCommentLike = stepLikeRepository.findByUserIdAndStepId(stepLike.getUserId(), stepLike.getStepId());
        if (optionalOldCommentLike.isPresent()) {
            StepLike oldStepLike = optionalOldCommentLike.get();
            oldStepLike.setValue(stepLike.getValue());
            stepLike = oldStepLike;
        }
        try {
            stepLike = stepLikeRepository.save(stepLike);
            stepLikeAsyncService.update(stepLike.getStepId());
        } catch (DataIntegrityViolationException ex) {
            if (firstAttempt) {
                stepLike = save(stepLike, false);
            }
        }
        return stepLike;
    }
}
