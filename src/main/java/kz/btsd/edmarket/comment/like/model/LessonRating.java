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
public class LessonRating {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lesson_like_seq")
    @SequenceGenerator(name = "lesson_like_seq", sequenceName = "lesson_like_seq",
            allocationSize = 1)
    private Long id;
    //ссылка на урок
    private Long lessonId;
    private Long userId;
    private Long useful;
    private Long interest;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public LessonRating() {
    }

    public LessonRating(Long userId, Long lessonId) {
        this.userId = userId;
        this.lessonId = lessonId;
    }
}
