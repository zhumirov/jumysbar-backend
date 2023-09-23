package kz.btsd.edmarket.review.service;

import kz.btsd.edmarket.review.model.*;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ReviewService {

    Review findById(Long id);

    List<ReviewDto> findAllByEventId(Long eventId, Sort sort);

    Review save(ReviewRequest reviewRequest);

    RatingsDto getRatingsByEventId(Long eventId);

    ReviewSection saveReviewSection(ReviewSectionRequest reviewRequest);
}
