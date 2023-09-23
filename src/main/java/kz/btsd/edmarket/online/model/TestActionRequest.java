package kz.btsd.edmarket.online.model;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

/**
 * Раздел
 */
@Data
public class TestActionRequest {
    private Long userId;
    private Long cardId;
    private List<String> answers;

    public TestActionRequest() {
    }
}
