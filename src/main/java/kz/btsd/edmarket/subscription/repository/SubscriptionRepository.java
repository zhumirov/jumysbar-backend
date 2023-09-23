package kz.btsd.edmarket.subscription.repository;

import kz.btsd.edmarket.online.subscription.OnlineSubscriptionDto;
import kz.btsd.edmarket.subscription.model.Subscription;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
    List<Subscription> findAll(Pageable pageable);

    List<Subscription> findByCreatedDateAfter(Date createdDate, Pageable pageable);

    long countByCreatedDateAfter(Date createdDate);

    long countByEventId(Long eventId);

    long countByPlanId(Long planId);

    long countByPromocodeId(Long promocodeId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<Subscription> findByPhoneAndEventId(String phone, Long eventId);

    Optional<Subscription> findByUserIdAndEventId(Long userId, Long eventId);

    @Transactional
    @Modifying
    void deleteByUserIdAndEventId(Long userId, Long eventId);

    @Query("select count(s) from Subscription s where s.userId = ?1 and s.eventId=?2")
    int countUserIdAndEventId(Long userId, Long eventId);

    List<Subscription> findAllByUserId(Long userId, Pageable pageable);

    @Query("select s.eventId from Subscription s where s.userId = ?1")
    List<Long> findEventIdByUserId(Long userId);

    @Query(value = "select new kz.btsd.edmarket.online.subscription.OnlineSubscriptionDto(s.id, s.eventId, e.fileId, s.userId, e.title, s.name, e.shortDescription, e.subsectionsSize, ep.viewedSubsectionsSize-ep.viewedExamSubsectionsSize, s.createdDate) from EventProgress ep " +
            "join Subscription s on ep.eventId = s.eventId and ep.userId = s.userId join Event e on ep.eventId = e.id where ep.userId=?1 and e.parentId is not null and e.status = 'APPROVED'")
    List<OnlineSubscriptionDto> findAllOnlineByUserId(Long userId, Pageable pageable);

    List<Subscription> findAllByEventId(Long eventId, Pageable pageable);

    List<Subscription> findAllByEventId(Long eventId, Sort sort);

    List<Subscription> findAllByEventId(Long eventId);

    @Query("select s from Subscription s join Event e on s.eventId = e.id where e.userId=?1")
    List<Subscription> findAllByOwnerId(Long ownerId, Pageable pageable);
}
