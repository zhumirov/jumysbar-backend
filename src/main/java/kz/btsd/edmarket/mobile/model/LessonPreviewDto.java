package kz.btsd.edmarket.mobile.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public class LessonPreviewDto {
    private Long id;
    private String title;
    private Long photoId;
    private int progress;
    private boolean hasOpen;
}
