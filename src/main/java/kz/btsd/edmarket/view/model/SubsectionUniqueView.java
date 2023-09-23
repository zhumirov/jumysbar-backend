package kz.btsd.edmarket.view.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * Количество уникальных просмотров карточки
 */
@Data
@Entity
public class SubsectionUniqueView {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_uniq_view_seq")
    @SequenceGenerator(name = "sub_uniq_view_seq", sequenceName = "sub_uniq_view_seq",
            allocationSize = 1)
    private Long id;
    private Long userId;
    private Long subsectionId;

    public SubsectionUniqueView() {
    }

    public SubsectionUniqueView(Long userId, Long subsectionId) {
        this.userId = userId;
        this.subsectionId = subsectionId;
    }
}
