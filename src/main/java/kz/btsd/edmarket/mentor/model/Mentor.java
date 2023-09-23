package kz.btsd.edmarket.mentor.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

/**
 * Ментор курса
 */
@Data
@Entity
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mentor_seq")
    @SequenceGenerator(name = "mentor_seq", sequenceName = "mentor_seq",
            allocationSize = 1)
    private Long id;
    //телефон ментора
    private String phone;
    private Long userId;
    private Long eventId;
    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public Mentor() {
    }

    public Mentor(String phone, Long eventId) {
        this.phone = phone;
        this.eventId = eventId;
    }

    public Mentor(String phone, Long eventId, Long userId) {
        this.phone = phone;
        this.eventId = eventId;
        this.userId = userId;
    }

    public boolean isRegistered() {
        return userId != null;
    }
}
