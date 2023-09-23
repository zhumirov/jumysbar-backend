package kz.btsd.edmarket.comment.model;

import lombok.Data;

@Data
public class CommentGradeDto {
    private Long id;
    //оценка домашнего задания
    private Long grade;

    public CommentGradeDto() {
    }
}
