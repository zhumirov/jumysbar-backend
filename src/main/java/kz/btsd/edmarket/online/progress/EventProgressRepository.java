package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.user.model.Platform;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventProgressRepository extends CrudRepository<EventProgress, Long> {
    long count();
    List<EventProgress> findByCreatedDateAfter(Date createdDate, Pageable pageable);
    List<EventProgress> findAll(Pageable pageable);
    long countByCreatedDateAfter(Date createdDate);

    @Transactional
    @Modifying
    @Query("UPDATE EventProgress ep SET ep.lastStepId = :lastStepId WHERE ep.userId=:userId and ep.eventId=:eventId")
    void updateLastStepId(@Param("eventId") Long eventId, @Param("userId") Long userId, @Param("lastStepId") Long lastStepId);

    @Query("SELECT sum(ep.viewedSubsectionsSize) FROM EventProgress ep where ep.userId = :userId")
    Integer sumViewedSubsectionsSizeByUserId(@Param("userId") Long userId);

    @Query("SELECT sum(ep.viewedSubsectionsSize) FROM EventProgress ep join Event e on e.id=ep.eventId where e.platform = :platform")
    Integer sumViewedSubsectionsSizeByPlatform(@Param("platform") Platform platform);

    @Query("SELECT ep.userId FROM EventProgress ep join Event e on e.id=ep.eventId " +
            "where e.platform=:platform GROUP BY ep.userId ORDER BY SUM(ep.viewedSubsectionsSize) DESC")
    List<Long> topViewedSubsectionsSizeByPlatform(@Param("platform") Platform platform, Pageable pageable);

    Optional<EventProgress> findByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT ep FROM EventProgress ep where (select count(s.id) from Subscription s where s.userId=ep.userId and s.eventId=ep.eventId) > 0 and ep.eventId=:eventId")
    List<EventProgress> findByEventIdSubscription(@Param("eventId") Long eventId, Pageable pageable);

    List<EventProgress> findByEventIdAndUserIdIn(Long eventId, List<Long> userIds);

    @Query("SELECT count(ep) FROM EventProgress ep where (select count(s.id) from Subscription s where s.userId=ep.userId and s.eventId=ep.eventId) > 0 and ep.eventId=:eventId")
    long countByEventIdSubscription(@Param("eventId") Long eventId);

    List<EventProgress> findByEventId(Long eventId);
}
