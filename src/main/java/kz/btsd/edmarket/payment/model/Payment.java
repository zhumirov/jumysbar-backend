package kz.btsd.edmarket.payment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * payment kassa24
 * https://wiki.kassa24.kz/index.php?title=%D0%A2%D0%B5%D1%85%D0%BD%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B0%D1%8F_%D0%B4%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D0%B0%D1%86%D0%B8%D1%8F_%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D1%8B_Ecommerce24
 */
@Data
@Entity
public class Payment {
    @Id
    private String id; // id-транзакции в ответ
    private String url; // url-в в ответ
    private String merchantId;    //Идентификатор мерчанта в системе Ecommerce (поле логина)	Да
    private Long amount;    //Сумма платежа в тиынах	Да
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;    //Номер платежа в системе принимающей стороны	Да
    private String returnUrl;    //URL-адрес, на который будет перенаправлен пользователь после оплаты	Нет
    private String callbackUrl;    //URL-адрес, на который будет отправлена информация после изменения платежа	Нет
    private String description;    //Параметр, являющий собой короткое описания платежа. Если указан, то будет отображен пользователю на форме оплаты	Нет
    //    private Object metadata;    //Параметр, в котором могут содержаться любые поля и значения, необходимые мерчанту для приема и обработки платежа на своей стороне при получении callback’а, т. е. содержимое metadata без изменений будет отправлено в callback после завершения обработки платежа в системе Ecommerce24	Нет
    private boolean demo;    //Если true, то платеж будет произведен в демо-режиме, при котором средства не списываются со счета пользователя и не перечисляются на счет коммерсанта. По умолчанию false	Нет
    @Embedded
    private CustomerData customerData;    //Параметр, в котором могут содержаться элементы email (необязательно), phone (необязательно), которые будут автоматически подставлены в форму оплаты	Нет

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class CustomerData {
        private String email;
        private String phone;
    }
}
