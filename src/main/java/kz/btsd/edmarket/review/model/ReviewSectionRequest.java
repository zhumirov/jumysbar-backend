package kz.btsd.edmarket.review.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReviewSectionRequest {

    @NotNull
    private Long sectionId;

    @NotNull
    private Long userId;

    @Min(1)
    @Max(5)
    private Integer usefulRating;

    @Min(1)
    @Max(5)
    private Integer interestingRating;

    @Size(max = 255)
    private String text;
}
