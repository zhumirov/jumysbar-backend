package kz.btsd.edmarket.view.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * Количество просмотров карточки
 */
@Data
@Entity
public class SubsectionView {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_view_seq")
    @SequenceGenerator(name = "sub_view_seq", sequenceName = "sub_view_seq",
            allocationSize = 1)
    private Long id;
    private Long eventId;
    private Long subsectionId;
    private Long value;

    public SubsectionView() {
    }

    public SubsectionView(Long eventId, Long subsectionId, Long value) {
        this.eventId = eventId;
        this.subsectionId = subsectionId;
        this.value = value;
    }
}
