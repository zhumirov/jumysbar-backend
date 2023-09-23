package kz.btsd.edmarket.online.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import kz.btsd.edmarket.event.model.EntityStatus;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;
import java.util.List;

/**
 * Раздел
 */
@Data
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Subsection {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subsection_seq")
    @SequenceGenerator(name = "subsection_seq", sequenceName = "subsection_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private Long parentId;
    private Long sectionId;
    @Enumerated(EnumType.STRING)
    private EntityStatus status = EntityStatus.NEW;

    // название подраздела
    private String title;
    // описание подраздела
    private String description;
    private boolean free = false;
    //скрытый
    private Boolean hidden;

    // общее количество лайков, надо пересчитывать
    @Column(updatable = false)
    private Long likes = 0L;
    // общее количество дизлайков, надо пересчитывать
    @Column(updatable = false)
    private Long dislikes = 0L;

    //блоки
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<Unit> units;

    private Long position;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();


    public Subsection() {
    }
}
