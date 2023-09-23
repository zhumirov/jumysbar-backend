package kz.btsd.edmarket.online.model.test;

import lombok.Data;

import java.util.List;

/**
 * Раздел
 */
@Data
public class LessonTestStartRequest {
    private Long userId;
    private Long lessonId;
    public LessonTestStartRequest() {
    }
}
