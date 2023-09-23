package kz.btsd.edmarket.payment.check.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

/**
 * кассовый чек
 */
@Data
@Entity
public class KassaCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kassa_check_seq")
    @SequenceGenerator(name = "kassa_check_seq", sequenceName = "kassa_check_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private Long eventId;
    private String url;
    private String receipt;
    private Long idDocument;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public KassaCheck() {
    }
}
