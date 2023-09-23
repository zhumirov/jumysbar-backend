package kz.btsd.edmarket.user.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

@Accessors(chain = true)
@Data
@Entity(name = "email_verification")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_ver_seq")
    @SequenceGenerator(name = "email_ver_seq", sequenceName = "email_ver_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private String uuid;
    private boolean confirmed = false;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public EmailVerification() {
    }
    public EmailVerification(Long userId, String uuid) {
        this.userId = userId;
        this.uuid = uuid;
    }
}
