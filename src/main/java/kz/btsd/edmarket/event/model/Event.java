package kz.btsd.edmarket.event.model;

import kz.btsd.edmarket.event.moderation.EventModeration;
import kz.btsd.edmarket.event.promocode.Promocode;
import kz.btsd.edmarket.user.model.Platform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * для elastic.elastic приоритеты поиска
 * 4-title
 * 3-shortDescription, description
 * 2-courseOutline,acquiredSkills,city,certification
 */
@Data
@Builder
@AllArgsConstructor
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
    @SequenceGenerator(name = "event_seq", sequenceName = "event_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private Long parentId;
    @Enumerated(EnumType.STRING)
    private Platform platform = Platform.JUMYSBAR;
    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.DRAFT;
    //причина блокировки
    private String reason;
    //2 - Основная информация
    //Название ивента
    private String title;
    //Краткое описание
    private String shortDescription;
    //Описание
    private String description;
    //Категория
    private Long categoryId=1L;
    //Кому подойдет
    private Long levelId=1L;
    //Основное изображение
    private String fileId;

    private String introVideoUrl;
    //Стоимость
    @JoinColumn(name = "eventId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Plan> plans;

    //Дата начала
    private String startDate;
    //Дата окончания
    private String endDate;
    //календарный курс
    private boolean calendar=false;

    //Модерация
    //todo вынести в дто
    @Transient
    private EventModeration moderation;

    private String webinarUrl;

    //4-Подробности о программе

    //Программа обучениия
    private String courseOutline;

    //Приобретаемые навыки
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<String> acquiredSkills;

    //Требования
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<String> requirements;

    //Для кого этот курс
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<String> auditory;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<TitleAndDescription> aboutCourse;

    // Этот курс подойдет
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<TitleAndDescription> courseFits;

    //Документы в конце обучения
    private String certification;
    // включены лайки/комментарии
    private Boolean commentLikes;
    private boolean sequentialAccess = false;

    //5-Контактная информация
    private String phone;
    private String email;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    private Integer subsectionsSize = 0;
    private Integer testsSize = 0;

    @JoinColumn(name = "eventId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Promocode> promoCodes;

    private String authorName;
    private String authorDescription;
    private String authorAvatarId;
    private String authorVideoUrl;

    private boolean showCourseContent;

    private String sampleVideoUrl;

    public boolean isPublishVersion() {
        return parentId != null;
    }

    public Event() {
    }
}
