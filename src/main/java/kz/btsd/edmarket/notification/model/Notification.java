package kz.btsd.edmarket.notification.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

/**
 * Уведомления
 */
@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    @SequenceGenerator(name = "notification_seq", sequenceName = "notification_seq",
            allocationSize = 1)
    private Long id;
    //получатель
    private Long userId;
    //инициатор сообщения
    private Long senderId;
    //текст уведомления
    private String text;
    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.NEW;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public Notification() {
    }

    public Notification(Long userId, Long senderId, String text) {
        this.userId = userId;
        this.senderId = senderId;
        this.text = text;
    }
}
