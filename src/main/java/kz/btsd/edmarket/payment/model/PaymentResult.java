package kz.btsd.edmarket.payment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity(name = "payment_result")
public class PaymentResult {
    @Id
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;    //Номер платежа в системе принимающей стороны	Да
    private String merchantId;    //Идентификатор мерчанта в системе Ecommerce (поле логина)	Да //todo нету в ответе, возможно это id
    private Long amount;    //Сумма платежа в тиынах	Да
    private Long commission; //Комиссия с транзакции указанна в тиынах
    private Boolean commissionIncluded; //Тип комиссии. Если true, то комиссия была взята с клиента, иначе — с мерчанта
    private Long attempt; //Номер попытки оплаты
    private String returnUrl;    //URL-адрес, на который будет перенаправлен пользователь после оплаты	Нет
    private String callbackUrl;    //URL-адрес, на который будет отправлена информация после изменения платежа	Нет
    private String description;    //Параметр, являющий собой короткое описания платежа. Если указан, то будет отображен пользователю на форме оплаты	Нет //todo нету в ответе
    private Date date;
    private Date dateOut;
    private Long status; //0	Неуспешная транзакция; 1	Успешная транзакция; 2	Сумма успешно заблокирована (для двухэтапных транзакций); 3	Транзакция отменена или был совершен возврат //todo сделать enum
    private Long errCode;//Содержит текст, кратко описывающий ошибку;     0	Ошибки нет; 220	Транзакция обработана в ошибку, информация об ошибке находится в поле errMessage; 230	Время жизни транзакции истекло
    private String errMessage; // status=220 Транзакция обработана в ошибку, информация об ошибке находится в поле errMessage;
    //    private Object metadata;	//Параметр, в котором могут содержаться любые поля и значения, необходимые мерчанту для приема и обработки платежа на своей стороне при получении callback’а, т. е. содержимое metadata без изменений будет отправлено в callback после завершения обработки платежа в системе Ecommerce24	Нет
    private boolean demo;    //Если true, то платеж будет произведен в демо-режиме, при котором средства не списываются со счета пользователя и не перечисляются на счет коммерсанта. По умолчанию false	Нет //todo нету в ответе
    private Payment.CustomerData customerData;    //Параметр, в котором могут содержаться элементы email (необязательно), phone (необязательно), которые будут автоматически подставлены в форму оплаты	Нет //todo нету в ответе

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    @AllArgsConstructor
    @Data
    public static class CustomerData {
        private String email;
        private String phone;
    }
}
