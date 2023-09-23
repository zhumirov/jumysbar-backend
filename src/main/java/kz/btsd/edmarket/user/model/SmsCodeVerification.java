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
@Entity(name = "sms_code_verification")
public class SmsCodeVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registration_seq")
    @SequenceGenerator(name = "registration_seq", sequenceName = "registration_seq",
            allocationSize = 1)
    private Long id;
    private String phone;
    private Long userId;
    private VerificationType type;
    private Long smsCode;
    private Long expiration;
    private boolean confirmed = false;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();
}
