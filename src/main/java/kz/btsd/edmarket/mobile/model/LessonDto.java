package kz.btsd.edmarket.mobile.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class LessonDto {
    private Long id;
    private String title;
    private String description;
    private Boolean hasSubscription;
    private Boolean hasFreeSlides;
    private Integer lastIdx;
    private List<SlideDto> slides;
}
