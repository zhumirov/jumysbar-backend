package kz.btsd.edmarket.comment.like.model;

import lombok.Data;

@Data
public class CellLessonRatingValueDto {
    private Double useful;
    private Double interest;
    private Long countUseful;
    private Long countInterest;
    private Long comments;
    private Long lessonId;
    private String lessonTitle;

    public CellLessonRatingValueDto(Double useful, Double interest, Long countUseful, Long countInterest) {
        this.useful = useful;
        this.interest = interest;
        this.countUseful = countUseful;
        this.countInterest = countInterest;
    }
}
