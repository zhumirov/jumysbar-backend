package kz.btsd.edmarket.webkassa.model.auth;

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
public class WebkassaToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_seq")
    @SequenceGenerator(name = "subscription_seq", sequenceName = "subscription_seq",
            allocationSize = 1)
    private Long id;
    private Long eventId;
    // userId подписчика
    private Long userId;

    private Long price;
    //todo нельзя удалять Plan если уже подписался, добавить not null и переделать удаление
    private Long planId;

    //todo title, name, phone,email - дублирующаяся информация если подписаться сможет только зарегистрированный пользователь
    private String title;
    private String name;
    private String phone;
    private String email;
    private Long promocodeId;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public WebkassaToken() {
    }

    public WebkassaToken(Long eventId, String title, String name, String phone, String email) {
        this.eventId = eventId;
        this.title = title;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

}
