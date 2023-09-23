package kz.btsd.edmarket.review.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReviewRequest {

    @NotNull
    private Long eventId;

    @NotNull
    private Long userId;

    @Size(max = 255)
    private String whatGood;

    @Size(max = 255)
    private String whatImprove;

    @Size(max = 255)
    private String recommendations;

    @Min(1)
    @Max(5)
    private Long rating;

    public ReviewRequest() {
    }
}
