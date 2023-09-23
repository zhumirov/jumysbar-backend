package kz.btsd.edmarket.certificate.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

/**
 * Настройки сертификата
 */
@Data
@Entity
public class CertificateSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cert_set_seq")
    @SequenceGenerator(name = "cert_set_seq", sequenceName = "cert_set_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private Long eventId;
    //Название сертификата
    private String certName;
    //Название курса
    private String title;
    //Описание курса
    private String description;
    //подтверждение получения сертификата
    private String confirmInfo;
    //фио организатор курса
    private String authorName;
    //фио организатор курса
    private String authorName2;
    //фио организатор курса
    private String authorName3;
    //должность
    private String position;
    //должность 2
    private String position2;
    //должность 3
    private String position3;
    //максимальный балл
    private boolean totalResult;
    //статистика по домашним работам
    private boolean homework;
    //статистика по экзаменам
    private boolean exam;
    //статистика по экзаменам - доля
    private boolean percentageExam;
    //подпись
    private boolean signature;
    //подписть организатора
    private String fileId;
    //подписть организатора
    private String fileId2;
    //подписть организатора
    private String fileId3;
    //логотип
    private String logoFileId;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public CertificateSettings() {
    }
}
