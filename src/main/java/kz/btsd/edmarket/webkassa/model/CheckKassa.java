package kz.btsd.edmarket.webkassa.model;

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
public class CheckKassa {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "check_kassa_seq")
    @SequenceGenerator(name = "check_kassa_seq", sequenceName = "check_kassa_seq",
            allocationSize = 1)
    private Long id;
    private String checkNumber;
    private Boolean published;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public CheckKassa(String checkNumber, Boolean published) {
        this.checkNumber = checkNumber;
        this.published = published;
    }

    public CheckKassa() {

    }

}
