package kz.btsd.edmarket.review.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

@Data
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_seq")
    @SequenceGenerator(name = "review_seq", sequenceName = "review_seq",
            allocationSize = 1)
    private Long id;
    private Long eventId;
    // userId подписчика
    private Long userId;

    private String whatGood;
    private String whatImprove;
    private String recommendations;

    private Long rating;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public Review() {
    }
}
