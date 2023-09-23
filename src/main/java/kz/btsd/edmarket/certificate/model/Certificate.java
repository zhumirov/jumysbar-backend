package kz.btsd.edmarket.certificate.model;

import kz.btsd.edmarket.user.model.UserShortDto;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Сертификат
 */
@Data
@Entity
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cert_seq")
    @SequenceGenerator(name = "cert_seq", sequenceName = "cert_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    @Transient
    private UserShortDto user;
    private Long eventId;

    //статистика по экзаменам
    private double testSize;
    private double resolvedTestSize;
    //статистика по домашним работам
    private int homeworkSize;
    private int resolvedHomeworkSize;
    //максимальный балл
    private double totalResult;
    private double resolvedTotalResult;
    //статистика по экзаменам - доля
    private double percentageTestSize;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public Certificate() {
    }

    public Certificate(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

}
