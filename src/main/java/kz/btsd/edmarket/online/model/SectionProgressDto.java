package kz.btsd.edmarket.online.model;

import lombok.Data;

import java.util.List;

/**
 * Раздел
 */
@Data
public class SectionProgressDto {
    private Long id;
    private Long userId;
    private Long position;
    private Long moduleId;
    // название раздела
    private String title;
    // проходной балл на экзамене
    private Long passingScore;

    private double testSize;
    private double resolvedTestSize;
    private int homeworkSize;
    private int resolvedHomeworkSize;
    private Long lessonProgressId;

    private List<SubsectionProgressDto> subsections;

    public SectionProgressDto() {
    }
}
