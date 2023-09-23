package kz.btsd.edmarket.online.progress;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import kz.btsd.edmarket.online.model.SubsectionDto;
import kz.btsd.edmarket.online.model.test.StepTestAnswer;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Хранение выполнененых тестов и просмотренных разделов
 * Создается после подписание на онлайн курс.
 */
@Data
public class LessonProgressDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private Long lessonId;

    //список вопросов экзамена
    private List<SubsectionDto> subsections = new ArrayList<>();

    //ответы пользователя на экзамене
    @Type(type = "jsonb")
    private List<StepTestAnswer> answers = new ArrayList<>();

    @JsonFormat(timezone = "GMT+06:00")
    private Date createdDate = new Date();

    @JsonFormat(timezone = "GMT+06:00")
    private Date finishedDate;

    private String clientIP;

    public LessonProgressDto() {
    }

    public LessonProgressDto(Long userId, Long eventId, Long lessonId) {
        this.userId = userId;
        this.eventId = eventId;
        this.lessonId = lessonId;
    }
}
