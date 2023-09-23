package kz.btsd.edmarket.online.model.test;

import lombok.Data;

import java.util.List;

/**
 * Раздел
 */
@Data
public class LessonTestActionRequest {
    private Long lessonProgressId;
    private List<StepTestAnswer> stepTests;
    public LessonTestActionRequest() {
    }
}
