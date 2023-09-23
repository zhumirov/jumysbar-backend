package kz.btsd.edmarket.event.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Тарифный план
 */
@Data
@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plan_seq")
    @SequenceGenerator(name = "plan_seq", sequenceName = "plan_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private Long parentId;
    private Long eventId;
    //Название пакета
    @Size(max = 50)
    private String title;
    //Описание пакета
    private String description;
    //Стоимость
    private Long price;
    @Enumerated(EnumType.STRING)
    private EntityStatus status = EntityStatus.NEW;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public Plan() {
    }
}
