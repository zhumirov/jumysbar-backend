package kz.btsd.edmarket.online.module.model;

import kz.btsd.edmarket.event.model.EntityStatus;
import kz.btsd.edmarket.online.model.Section;
import kz.btsd.edmarket.online.model.Subsection;
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
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "module_seq")
    @SequenceGenerator(name = "module_seq", sequenceName = "module_seq",
            allocationSize = 1)
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
    private boolean calendar=false;
    @JoinColumn(name = "moduleId")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public Module() {
    }

    public Module(Long id, List<Section> sections) {
        this.id = id;
        this.sections = sections;
    }
}
