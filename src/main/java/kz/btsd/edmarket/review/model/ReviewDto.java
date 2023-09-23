package kz.btsd.edmarket.review.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
public class ReviewDto {
    private Long id;
    private String author;
    private Long rating;
    private String whatGood;
    private String whatImprove;
    private String recommendations;
    private Date createdDate;
}
