package kz.btsd.edmarket.online.model;

import kz.btsd.edmarket.comment.like.model.LessonRating;
import kz.btsd.edmarket.event.model.EntityStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

/**
 * Раздел
 */
@Data
public class SectionFullDto {
    private Long id;
    private Long userId;
    private Long parentId;
    private Long eventId;
    private Long moduleId;
    private Long position;
    @Enumerated(EnumType.STRING)
    private EntityStatus status = EntityStatus.NEW;
    // название раздела
    private String title;
    // описание раздела
    private String description;
    private Integer subsectionsSize;
    private Integer viewedSubsectionsSize;
    private Long lessonProgressId;

    private Double useful = 0d;
    private Double interest = 0d;
    private LessonRating currentUserLessonRating;
    private boolean resolvedTest = false;

    @Enumerated(EnumType.STRING)
    private SectionType type = SectionType.LESSON;
    //длительность в минутах
    private Long duration;
    private Long examSize;
    private Long score = 10l;
    // проходной балл на экзамене
    private Long passingScore;
    //скрытый
    private Boolean hidden;
    //обратная связь в конце урока
    private Boolean review = false;
    //просмотр результатов экзамена
    private Boolean examReview = false;

    private List<SubsectionDto> subsections;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public SectionFullDto() {
    }
}
