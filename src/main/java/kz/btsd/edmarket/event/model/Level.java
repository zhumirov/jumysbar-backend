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
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "level_seq")
    @SequenceGenerator(name = "level_seq", sequenceName = "level_seq",
            allocationSize = 1)
    private Long id;
    private String rus;
    private String eng;
    private String kaz;

    public Level() {
    }
}
