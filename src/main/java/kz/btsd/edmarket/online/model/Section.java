package kz.btsd.edmarket.online.model;

import kz.btsd.edmarket.event.model.EntityStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import java.util.Date;
import java.util.List;

/**
 * Раздел
 */
@Data
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "section_seq")
    @SequenceGenerator(name = "section_seq", sequenceName = "section_seq",
            allocationSize = 1)
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
    private Double useful = 0d;
    private Double interest = 0d;

    @Enumerated(EnumType.STRING)
    private SectionType type = SectionType.LESSON;
    //длительность в минутах
    private Long duration;
    //количество тестов в экзамене
    private Long examSize;
    // количество баллов за экзамен
    private Long score = 10l;
    // проходной балл на экзамене
    private Long passingScore;
    //скрытый
    private Boolean hidden;
    //просмотр обратной связи в конце урока
    private Boolean review = false;
    //просмотр результатов экзамена
    private Boolean examReview = false;

    @JoinColumn(name = "sectionId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subsection> subsections;

    private Integer subsectionsSize = 0;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public Section() {
    }

    public Section(Long id, List<Subsection> subsections) {
        this.id = id;
        this.subsections = subsections;
    }
}
