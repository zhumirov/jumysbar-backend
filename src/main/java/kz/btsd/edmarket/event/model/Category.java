package kz.btsd.edmarket.event.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * Справочник - Кому подойдет
 */
@Data
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "category_seq",
            allocationSize = 1)
    private Long id;
    private String rus;
    private String eng;
    private String kaz;

    public Category() {
    }
}
