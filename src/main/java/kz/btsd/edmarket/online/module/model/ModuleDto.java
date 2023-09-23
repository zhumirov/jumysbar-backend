package kz.btsd.edmarket.online.module.model;

import kz.btsd.edmarket.event.model.EntityStatus;
import kz.btsd.edmarket.online.model.SectionFullDto;
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
public class ModuleDto {
    private Long id;
    private Long userId;
    private Long parentId;
    private Long eventId;
    private Long position;
    @Enumerated(EnumType.STRING)
    private EntityStatus status = EntityStatus.NEW;
    private boolean openAccess = true;
    //скрытый
    private Boolean hidden;
    // название раздела
    private String title;
    //Дата начала
    private String startDate;
    //Дата окончания
    private String endDate;
    //календарный курс
    private boolean calendar;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    private List<SectionFullDto> sections;

    public ModuleDto() {
    }
}
