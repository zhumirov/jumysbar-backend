package kz.btsd.edmarket.online.model.test;

import lombok.Data;

import java.util.List;

/**
 * Раздел
 */
@Data
public class StepTestActionRequest {
    private Long userId;
    private Long stepId;
    private List<String> answers;

    public StepTestActionRequest() {
    }

    public StepTestActionRequest(Long userId, Long stepId, List<String> answers) {
        this.userId = userId;
        this.stepId = stepId;
        this.answers = answers;
    }
}
