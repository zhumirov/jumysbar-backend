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
 * Информация о кассе
 */
@Data
@Entity
public class KassaInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kassa_info_seq")
    @SequenceGenerator(name = "kassa_info_seq", sequenceName = "kassa_info_seq",
            allocationSize = 1)
    private Long id;
    private String token;
    private Long idKkm;
    private Long idShift;
    private String uid;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public KassaInfo() {
    }

    public void clear() {
        createdDate = null;
        token = null;
        idKkm = null;
        idShift = null;
        uid = null;
    }
}
