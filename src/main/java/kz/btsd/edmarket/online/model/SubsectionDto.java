package kz.btsd.edmarket.online.model;

import kz.btsd.edmarket.comment.like.model.StepLike;
import kz.btsd.edmarket.event.model.EntityStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

/**
 * Раздел
 */
@Data
public class SubsectionDto {
    private Long id;
    private Long userId;
    private Long parentId;
    //секция
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
    private Long likes = 0L;
    // общее количество дизлайков, надо пересчитывать
    private Long dislikes = 0L;
    private StepLike currentUserStepLike;
    private boolean viewed = false;
    //блоки
    private List<Unit> units;

    private Long position;

    private Long views;

    private Long uniqueViews;

    @CreatedDate //todo - еше не работает
    private Date createdDate = new Date();

    public SubsectionDto() {
    }
}
