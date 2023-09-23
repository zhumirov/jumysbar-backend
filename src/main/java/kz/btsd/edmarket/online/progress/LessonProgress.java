package kz.btsd.edmarket.online.progress;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import kz.btsd.edmarket.online.model.test.StepTestAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Хранение выполнененых тестов и просмотренных разделов
 * Создается после подписание на онлайн курс.
 */
@AllArgsConstructor
@Builder
@Data
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "les_progress_seq")
    @SequenceGenerator(name = "les_progress_seq", sequenceName = "les_progress_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private Long eventId;
    private Long lessonId;

    //список вопросов экзамена
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<Long> subsections = new ArrayList<>();

    //ответы пользователя на экзамене
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<StepTestAnswer> answers = new ArrayList<>();

    @JsonFormat(timezone = "GMT+06:00")
    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    @JsonFormat(timezone = "GMT+06:00")
    private Date finishedDate;

    @Column(name = "client_ip")
    private String clientIP;

    public LessonProgress() {
    }

    public LessonProgress(Long userId, Long eventId, Long lessonId) {
        this.userId = userId;
        this.eventId = eventId;
        this.lessonId = lessonId;
    }
}
