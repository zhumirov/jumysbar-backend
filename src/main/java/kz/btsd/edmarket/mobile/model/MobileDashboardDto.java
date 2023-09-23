package kz.btsd.edmarket.mobile.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MobileDashboardDto {
    private MinimalUserDto user;
    private List<MinimalCourseDto> courses;
}
