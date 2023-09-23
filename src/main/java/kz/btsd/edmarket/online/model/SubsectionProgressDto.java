package kz.btsd.edmarket.online.model;

import lombok.Data;

/**
 * Раздел
 */
@Data
public class SubsectionProgressDto {
    private Long id;
    private Long userId;
    private Long position;
    private Long sectionId;
    private Long moduleId;
    // название подраздела
    private String title;
    private double testSize;
    private double resolvedTestSize;
    private int homeworkSize;
    private int resolvedHomeworkSize;

    private long commentLikes;
    private long commentDislikes;

    public SubsectionProgressDto() {
    }
}
