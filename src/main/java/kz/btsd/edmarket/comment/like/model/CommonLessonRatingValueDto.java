package kz.btsd.edmarket.comment.like.model;

import lombok.Data;

@Data
public class CommonLessonRatingValueDto {
    private Double useful;
    private Double interest;
    private Long countUseful;
    private Long countInterest;

    public CommonLessonRatingValueDto(Double useful, Double interest, Long countUseful, Long countInterest) {
        this.useful = useful;
        this.interest = interest;
        this.countUseful = countUseful;
        this.countInterest = countInterest;
    }
}
