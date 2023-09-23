package kz.btsd.edmarket.review.service.impl;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.review.model.*;
import kz.btsd.edmarket.review.repository.ReviewRepository;
import kz.btsd.edmarket.review.repository.ReviewSectionRepository;
import kz.btsd.edmarket.review.service.ReviewService;
import kz.btsd.edmarket.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewSectionRepository reviewSectionRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewConverter reviewConverter;
    private final UserService userService;

    @Override
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    @Override
    public List<ReviewDto> findAllByEventId(Long eventId, Sort sort) {
        return reviewRepository.findAllByEventId(eventId, sort)
                .stream()
                .map(review -> ReviewDto.builder()
                        .id(review.getId())
                        .author(userService.getFullName(review.getUserId()))
                        .rating(review.getRating())
                        .whatGood(review.getWhatGood())
                        .whatImprove(review.getWhatImprove())
                        .recommendations(review.getRecommendations())
                        .createdDate(review.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Review save(ReviewRequest reviewRequest) {
        List<Review> reviews = reviewRepository.findAllByUserIdAndEventId(reviewRequest.getUserId(),
                reviewRequest.getEventId());
        if (reviews.size() > 0) {
            throw new IllegalArgumentException("уже есть оценка-комментарий");
        }
        Review review = reviewConverter.convertToEntity(reviewRequest);
        return reviewRepository.save(review);
    }

    @Override
    public RatingsDto getRatingsByEventId(Long eventId) {
        return RatingsDto.builder()
                .rating(reviewRepository.avgRating(eventId))
                .ratingsCount(reviewRepository.countByEventId(eventId))
                .reviewsCount(reviewRepository.countByEventIdWithReviews(eventId))
                .oneStar(reviewRepository.countByEventIdAndRating(eventId, 1L))
                .twoStar(reviewRepository.countByEventIdAndRating(eventId, 2L))
                .threeStar(reviewRepository.countByEventIdAndRating(eventId, 3L))
                .fourStar(reviewRepository.countByEventIdAndRating(eventId, 4L))
                .fiveStar(reviewRepository.countByEventIdAndRating(eventId, 5L))
                .build();
    }

    @Override
    public ReviewSection saveReviewSection(ReviewSectionRequest request) {
        ReviewSection reviewSection = new ReviewSection();
        reviewSection.setSectionId(request.getSectionId());
        reviewSection.setText(request.getText());
        reviewSection.setInterestingRating(request.getInterestingRating());
        reviewSection.setUsefulRating(request.getUsefulRating());
        reviewSection.setUserId(request.getUserId());
        reviewSection.setCreatedDate(new Date());
        return reviewSectionRepository.save(reviewSection);
    }
}
