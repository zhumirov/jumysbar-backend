package kz.btsd.edmarket.review.repository;

import kz.btsd.edmarket.review.model.Review;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {

    int countByEventId(Long eventId);

    @Query("select count(r.id) from Review r where r.eventId=?1 " +
            "and (r.whatGood is not null " +
            "or r.whatImprove is not null " +
            "or r.recommendations is not null)")
    int countByEventIdWithReviews(Long eventId);

    @Query("SELECT avg(c.rating) FROM Review c where c.eventId=?1")
    Double avgRating(Long eventId);

    int countByEventIdAndRating(Long eventId, Long rating);

    List<Review> findAllByUserId(Long userId);

    //todo может быть только один
    List<Review> findAllByUserIdAndEventId(Long userId, Long eventId);

    List<Review> findAllByEventId(Long eventId, Sort sort);

}
