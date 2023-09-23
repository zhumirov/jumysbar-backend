package kz.btsd.edmarket.mobile.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MinimalCourseDto {
    private Long id;
    private String title;
    private String author;
    private String logoId;
}
