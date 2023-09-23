package kz.btsd.edmarket.event.promocode;

import kz.btsd.edmarket.event.model.EntityStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Промокод
 */
@Data
@Entity
public class Promocode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promocode_seq")
    @SequenceGenerator(name = "promocode_seq", sequenceName = "promocode_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private Long parentId;
    private Long eventId;
    //Название промокода
    private String title;
    //Вид скидки
    @NotNull
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    //Размер скидки
    private Long discountAmount;
    //Количество промокодов
    @NotNull
    @Enumerated(EnumType.STRING)
    private CodeNumberType codeNumberType;
    //Количество промокодов
    private Long codeNumber;
    //время начала
    @NotNull
    @Enumerated(EnumType.STRING)
    private PromoStartDate promoStartDate;
    //Дата начала
    private String startDate;
    // time todo подумать как хранить -- какой основной часовой пояс у нас?
    //Время начала
    private String startTime;
    //Дата начала
    private Date startDateTime;
    @Enumerated(EnumType.STRING)
    private EntityStatus status = EntityStatus.NEW;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public Promocode() {
    }
}
