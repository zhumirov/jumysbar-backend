package kz.btsd.edmarket.subscription.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

/**
 * userId подписчика; не обязательн потому что сейчас могут подписываться не зарегистрированные пользовватели
 * удалить поля name, phone, email если все подпискики будут зарегистрированы.
 * //todo удалить title
 */
@Data
@Entity
public class EmailMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_message_seq")
    @SequenceGenerator(name = "email_message_seq", sequenceName = "email_message_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private Long eventId;
    private String message;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public EmailMessage() {
    }
}
