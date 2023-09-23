package kz.btsd.edmarket.comment.model;

import kz.btsd.edmarket.comment.like.model.CommentLike;
import kz.btsd.edmarket.comment.like.model.StepLike;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @SequenceGenerator(name = "comment_seq", sequenceName = "comment_seq",
            allocationSize = 1)
    private Long id;
    //ссылка на шаг
    private Long stepId;
    //ссылка на урок
    private Long lessonId;
    private Long userId;
    //ссылка на первый комментарий на который отвечают
    private Long threadId;
    //прикрепленный файл, изображение, документ.
    //todo тип массив String[] - не поддерживается hibernate, может стоит сделать соц сети по типам
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> files = new HashSet<>();
    //ответ на комментарий с id
    private Long answerToId;

    private String text;
    // общее количество лайков, надо пересчитывать
    @Column(updatable = false)
    private Long likes = 0L;
    // общее количество дизлайков, надо пересчитывать
    @Column(updatable = false)
    private Long dislikes = 0L;

    //оценка домашнего задания
    private Long grade;

    private boolean homework = false;

    //Статус
    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.NEW;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    @LastModifiedDate //todo - еше не работает
    private Date lastModifiedDate;

    @JoinColumn(name = "commentId")
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<CommentLike> commentLikes;

    public Comment() {
    }
}
