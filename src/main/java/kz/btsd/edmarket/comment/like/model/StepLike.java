package kz.btsd.edmarket.comment.like.model;

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

@Data
@Entity
public class StepLike {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "step_like_seq")
    @SequenceGenerator(name = "step_like_seq", sequenceName = "step_like_seq",
            allocationSize = 1)
    private Long id;
    //ссылка на урок
    private Long stepId;
    private Long userId;

    //Лайк-дизлайк
    @Enumerated(EnumType.STRING)
    private LikeValue value;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public StepLike() {
    }
}
