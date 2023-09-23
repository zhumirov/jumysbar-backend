package kz.btsd.edmarket.mobile.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class ContentDto {
    private Long id;
    private String title;
    private List<LessonPreviewDto> lessons;
}
