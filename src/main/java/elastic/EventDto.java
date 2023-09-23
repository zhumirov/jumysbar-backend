package elastic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.model.Plan;
import kz.btsd.edmarket.event.model.TitleAndDescription;
import kz.btsd.edmarket.event.promocode.Promocode;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static elastic.EventDto.INDEX_NAME;

/**
 * для elastic.elastic приоритеты поиска
 * 4-title
 * 3-shortDescription, description
 * 2-courseOutline,acquiredSkills,city,certification
 * 1-category,subCategory,city,address
 */
@Document(indexName = INDEX_NAME)
@Data
@Builder
@AllArgsConstructor
public class EventDto {
    public static final String INDEX_NAME = "events_v3";
    @Id
    private Long id;
    private Long userId;
    private Long parentId;
    @Enumerated(EnumType.STRING)
    private Platform platform = Platform.JUMYSBAR;
    private UserDto user;
    @Enumerated(EnumType.STRING)
    private EventStatus status;
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
    @Field(type = FieldType.Keyword)
    private String fileId;
    private String introVideoUrl;
    //Стоимость
    private Set<Plan> plans;
    //Дата начала
    private String startDate;
    //Дата окончания
    private String endDate;
    //календарный курс
    private boolean calendar;

    private String webinarUrl;

    //4-Подробности о программе

    //Программа обучениия
    private String courseOutline;
    //Приобретаемые навыки
    private List<String> acquiredSkills;
    //Документы в конце обучения
    private String certification;
    //сушествует сертификат
    private Boolean cert;
    // включены лайки/комментарии
    private Boolean commentLikes = true;
    //бесплатный курс
    private Boolean free;
    private boolean sequentialAccess = false;

    private List<String> requirements;
    private List<String> auditory;
    private List<TitleAndDescription> aboutCourse;
    private List<TitleAndDescription> courseFits;

    //5-Контактная информация
    private String phone;
    private String email;

    @JsonFormat(timezone = "GMT+06:00")
    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    private Integer subsectionsSize;
    private Integer testsSize;

    //todo перенести в dto
    private Set<Promocode> promoCodes;

    private String authorName;
    private String authorDescription;
    private String authorAvatarId;
    private String authorVideoUrl;

    private boolean showCourseContent;
    private String sampleVideoUrl;

    public EventDto() {
    }
}
