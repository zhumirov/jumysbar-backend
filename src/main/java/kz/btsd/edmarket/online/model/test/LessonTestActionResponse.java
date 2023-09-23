package kz.btsd.edmarket.online.model.test;

import kz.btsd.edmarket.online.model.TestActionResponse;
import kz.btsd.edmarket.online.progress.LessonProgress;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Раздел
 */
@Data
public class LessonTestActionResponse {
    private LessonProgress lessonProgress;
    private List<TestActionResponse> stepTestResults = new ArrayList<>();
    public LessonTestActionResponse() {
    }
}
