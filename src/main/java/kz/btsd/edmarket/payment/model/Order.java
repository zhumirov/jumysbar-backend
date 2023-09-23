package kz.btsd.edmarket.payment.model;

import kz.btsd.edmarket.event.promocode.DiscountType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

/**
 * Заказ
 */
@Data
@Entity(name = "order_ed")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq",
            allocationSize = 1)
    private Long id;
    //приобретаемый курс
    private Long eventId;
    //тариф курса
    private Long planId;
    //покупатель курса
    private Long userId;
    private String paymentId;
    private String paymentResultId;
    private Long price;
    private Long promocodeId;
    //Вид скидки
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    //Размер скидки
    private Long discountAmount;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();
}
