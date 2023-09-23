package kz.btsd.edmarket.notification.model;

import kz.btsd.edmarket.user.model.UserDto;
import kz.btsd.edmarket.user.model.UserShortDto;
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
public class NotificationDto {
    private Long id;
    //получатель
    private Long userId;
    private Long senderId;
    private UserShortDto sender;
    //текст уведомления
    private String text;
    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.NEW;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public NotificationDto() {
    }

    public NotificationDto(Long userId, Long senderId, String text) {
        this.userId = userId;
        this.senderId = senderId;
        this.text = text;
    }
}
