package kz.btsd.edmarket.comment.like.model;

import lombok.Data;

@Data
public class LessonRatingValueDto {
    private Long id;
    //ссылка на урок
    private Long lessonId;
    private Long userId;
    private Long value;
    private LessonRatingType type;

    public LessonRatingValueDto() {
    }
}
