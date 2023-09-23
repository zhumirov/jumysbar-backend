package kz.btsd.edmarket.review.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RatingsDto {
    private Double rating;
    private Integer reviewsCount;
    private Integer ratingsCount;
    private Integer oneStar;
    private Integer twoStar;
    private Integer threeStar;
    private Integer fourStar;
    private Integer fiveStar;
}
