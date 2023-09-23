package kz.btsd.edmarket.comment.like.controller;


import kz.btsd.edmarket.comment.like.model.StepLike;
import kz.btsd.edmarket.comment.like.repository.StepLikeRepository;
import kz.btsd.edmarket.comment.like.service.StepLikeService;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
public class StepLikeController {
    @Autowired
    private StepLikeRepository stepLikeRepository;
    @Autowired
    private StepLikeService stepLikeService;
    @Autowired
    private AuthService authService;

    @GetMapping("/step-likes/{id}")
    public StepLike findById(@PathVariable Long id) {
        return stepLikeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    @PostMapping("/step-likes")
    public StepLike save(Authentication authentication, @Valid @RequestBody StepLike stepLike) {
        authService.checkOwner(authentication.getName(), stepLike.getUserId());
        return stepLikeService.save(stepLike, true);
    }

    @PutMapping("/step-likes/{id}")
    public StepLike changeEvent(Authentication authentication, @RequestBody StepLike stepLike, @PathVariable Long id) {
        authService.checkOwner(authentication.getName(), stepLike.getUserId());
        return stepLikeService.save(stepLike, true);
    }
}
