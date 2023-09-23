package kz.btsd.edmarket.review.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class ReviewSection {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_section_seq")
    @SequenceGenerator(name = "review_section_seq", sequenceName = "review_section_seq",
            allocationSize = 1)
    private Long id;
    private Long sectionId;
    private Long userId;
    private Integer usefulRating;
    private Integer interestingRating;
    private String text;
    private Date createdDate;
}
