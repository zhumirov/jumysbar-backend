package kz.btsd.edmarket.review.controller;

import kz.btsd.edmarket.review.model.*;
import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.review.service.ReviewService;
import kz.btsd.edmarket.user.model.UserDto;
import kz.btsd.edmarket.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

@AllArgsConstructor
@CrossOrigin(origins = "*")
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/.well-known/pki-validation/D9EB873874B137905794D63EE0759529.txt")
    public String findById() {
        return "8102880AD2D3CB80F8439442A3CB8E1F05CFCEB6CE81114A6F6D669491142AFD\n" +
                "comodoca.com\n" +
                "540ac2cffd2a221";
    }

    @GetMapping("/review/{id}")
    public Review findById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @PostMapping("/review")
    public Review save(@AuthenticationPrincipal Jwt jwt,
                       @Valid @RequestBody ReviewRequest reviewRequest) {
        UserDto user = userService.findByIdtoDto(jwt.getSubject());
        if (!user.getId().equals(reviewRequest.getUserId())) {
            throw new IllegalArgumentException("Нельзя создавать отзыв за другого пользователя");
        }
        return reviewService.save(reviewRequest);
    }

    @GetMapping("/review/rating/{eventId}")
    public RatingsDto stats(@PathVariable Long eventId) {
        return reviewService.getRatingsByEventId(eventId);
    }

    @GetMapping("/review")
    public List<ReviewDto> allByEventId(@RequestParam Long eventId,
                                        @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                        @RequestParam(defaultValue = "asc", required = false) String order) {
        return reviewService.findAllByEventId(eventId, SortUtils.buildSort(sort, order));
    }

    @PostMapping("/review-section")
    public ReviewSection saveReviewSection(@AuthenticationPrincipal Jwt jwt,
                                    @Valid @RequestBody ReviewSectionRequest reviewRequest) {
        UserDto user = userService.findByIdtoDto(jwt.getSubject());
        if (!user.getId().equals(reviewRequest.getUserId())) {
            throw new IllegalArgumentException("Нельзя создавать отзыв за другого пользователя");
        }
        return reviewService.saveReviewSection(reviewRequest);
    }
}
