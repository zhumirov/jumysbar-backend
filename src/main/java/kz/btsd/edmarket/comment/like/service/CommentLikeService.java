package kz.btsd.edmarket.comment.like.service;

import kz.btsd.edmarket.comment.like.model.CommentLike;
import kz.btsd.edmarket.comment.like.repository.CommentLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentLikeService {
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private CommentLikeAsyncService commentLikeAsyncService;

    //todo переделать на апдейт в один шаг через базу
    public CommentLike save(CommentLike commentLike, boolean firstAttempt) {
        Optional<CommentLike> optionalOldCommentLike = commentLikeRepository.findByUserIdAndCommentId(commentLike.getUserId(), commentLike.getCommentId());
        if (optionalOldCommentLike.isPresent()) {
            CommentLike oldCommentLike = optionalOldCommentLike.get();
            oldCommentLike.setValue(commentLike.getValue());
            commentLike = oldCommentLike;
        }
        try {
            commentLike = commentLikeRepository.save(commentLike);
            commentLikeAsyncService.update(commentLike.getCommentId());
        } catch (DataIntegrityViolationException ex) {
            if (firstAttempt) {
                commentLike = save(commentLike, false);
            }
        }
        return commentLike;
    }
}
