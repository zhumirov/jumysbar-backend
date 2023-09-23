package kz.btsd.edmarket.comment.like.controller;


import kz.btsd.edmarket.comment.like.model.CommentLike;
import kz.btsd.edmarket.comment.like.repository.CommentLikeRepository;
import kz.btsd.edmarket.comment.like.service.CommentLikeService;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
public class CommentLikeController {
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private CommentLikeService commentLikeService;
    @Autowired
    private AuthService authService;

    @GetMapping("/comment-likes/{id}")
    public CommentLike findById(@PathVariable Long id) {
        return commentLikeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    @PostMapping("/comment-likes")
    public CommentLike save(Authentication authentication, @Valid @RequestBody CommentLike commentLike) {
        authService.checkOwner(authentication.getName(), commentLike.getUserId());
        return commentLikeService.save(commentLike, true);
    }

    @PutMapping("/comment-likes/{id}")
    public CommentLike changeEvent(Authentication authentication, @RequestBody CommentLike commentLike, @PathVariable Long id) {
        authService.checkOwner(authentication.getName(), commentLike.getUserId());
        return commentLikeService.save(commentLike, true);
    }
}
