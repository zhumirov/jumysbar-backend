package kz.btsd.edmarket.online.model.test;

import lombok.Data;

import java.util.List;

/**
 * Ответ на тест
 */
@Data
public class StepTestAnswer {
    private Long stepId;
    private List<String> answers;

    public StepTestAnswer() {
    }
}
