package kz.btsd.edmarket.comment.like.service;

import kz.btsd.edmarket.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CommentLikeAsyncService {
    @Autowired
    private CommentRepository commentRepository;

    //todo переделать на апдейт в один шаг через базу
    @Async
    public void update(Long commentId) {
        commentRepository.updateCommentLikeAndDislikeNative(commentId);
    }
}
