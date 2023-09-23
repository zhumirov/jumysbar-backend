package kz.btsd.edmarket.online.progress;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Хранение выполнененых тестов и просмотренных разделов
 * Создается после подписание на онлайн курс.
 */
@Data
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class EventProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "progress_seq")
    @SequenceGenerator(name = "progress_seq", sequenceName = "progress_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private Long eventId;
    private Long lastStepId;
    private String review;
    //список экзаменов
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Set<Long> sections = new HashSet<>();
    //список сабсекций
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Set<Long> subsections = new HashSet<>();
    @Column(name = "subsections_size")
    private Integer viewedSubsectionsSize = 0;
    @Column(name = "exam_subsections_size")
    private int viewedExamSubsectionsSize = 0;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Date createdDate = new Date();

    public EventProgress() {
    }
}
