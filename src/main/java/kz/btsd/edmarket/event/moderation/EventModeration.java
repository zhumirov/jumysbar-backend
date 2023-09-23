package kz.btsd.edmarket.event.moderation;

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
 * Модерация курса
 */
@Data
@Entity
public class EventModeration {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_mod_seq")
    @SequenceGenerator(name = "event_mod_seq", sequenceName = "event_mod_seq",
            allocationSize = 1)
    private Long id;
    private Long eventId;
    // userId создателя курса
    private Long userId;

    private String comment;

    @Enumerated(EnumType.STRING)
    private ModerationStatus status = ModerationStatus.NEW;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public EventModeration() {
    }

    public EventModeration(Long eventId, Long userId) {
        this.eventId = eventId;
        this.userId = userId;
    }

}
