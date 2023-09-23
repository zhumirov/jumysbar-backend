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
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "com_like_seq")
    @SequenceGenerator(name = "com_like_seq", sequenceName = "com_like_seq",
            allocationSize = 1)
    private Long id;
    //ссылка на комментарий
    private Long commentId;
    private Long userId;

    //Лайк-дизлайк
    @Enumerated(EnumType.STRING)
    private LikeValue value;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public CommentLike() {
    }
}
