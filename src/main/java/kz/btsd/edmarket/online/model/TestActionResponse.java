package kz.btsd.edmarket.online.model;

import lombok.Data;

import java.util.List;

/**
 * Раздел
 */
@Data
public class TestActionResponse {
    private boolean resolved = false;
    private Long stepId;

    public TestActionResponse(boolean resolved) {
        this.resolved = resolved;
    }

    public TestActionResponse(boolean resolved, Long stepId) {
        this.resolved = resolved;
        this.stepId = stepId;
    }
}
