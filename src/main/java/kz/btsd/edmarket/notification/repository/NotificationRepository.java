package kz.btsd.edmarket.notification.repository;

import kz.btsd.edmarket.notification.model.Notification;
import kz.btsd.edmarket.notification.model.NotificationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    long countByUserIdAndStatus(Long userId, NotificationStatus status);
    long countByUserId(Long userId);

    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status, Pageable pageable);
    List<Notification> findByUserId(Long userId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.status = kz.btsd.edmarket.notification.model.NotificationStatus.VIEWED WHERE n.id=:id")
    void viewed(@Param("id") Long id);
}
