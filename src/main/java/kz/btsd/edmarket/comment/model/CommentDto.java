package kz.btsd.edmarket.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kz.btsd.edmarket.comment.like.model.CommentLike;
import kz.btsd.edmarket.file.model.FileDto;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class CommentDto {
    private Long id;
    //ссылка на урок
    private Long stepId;
    //ссылка на урок
    private Long lessonId;
    private Long userId;
    private UserCommentDto user;
    //ссылка на первый комментарий на который отвечают
    private Long threadId;
    //прикрепленный файл, изображение, документ.
    //todo тип массив String[] - не поддерживается hibernate, может стоит сделать соц сети по типам
    private Set<FileDto> fileInfos = new HashSet<>();
    private Set<String> files = new HashSet<>();
    //ответ на комментарий с id
    private Long answerToId;
    @JsonIgnore
    private CommentDto answerTo;
    private List<CommentDto> answers;

    private String text;
    // общее количество лайков, надо пересчитывать
    private Long likes = 0L;
    // общее количество дизлайков, надо пересчитывать
    private Long dislikes = 0L;
    //оценка домашнего задания
    private Long grade;
    private CommentLike currentUserCommentLike;
    private boolean homework = false;

    //Статус
    private CommentStatus status = CommentStatus.NEW;

    private Date createdDate = new Date();

    private Date lastModifiedDate;

    public CommentDto() {
    }
}
