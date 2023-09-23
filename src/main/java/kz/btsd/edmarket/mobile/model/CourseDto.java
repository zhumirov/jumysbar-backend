package kz.btsd.edmarket.mobile.model;

import kz.btsd.edmarket.event.model.Plan;
import kz.btsd.edmarket.event.model.TitleAndDescription;
import kz.btsd.edmarket.review.model.ReviewDto;
import kz.btsd.edmarket.review.model.RatingsDto;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class CourseDto {
    private Long id;
    private String title;
    private String description;
    private boolean hasSubscription;
    private AuthorDto author;
    private String previewLink;
    private List<String> langs;
    private Date updated;
    private Long students;
    private List<TitleAndDescription> courseAbout;
    private List<String> acquiredSkills;
    private List<String> requirements;
    private List<String> auditory;
    private List<ContentDto> content;
    private List<ReviewDto> reviews;
    private List<PlanDto> plans;
    private RatingsDto ratings;

    private List<TitleAndDescription> aboutCourse;
    private List<TitleAndDescription> courseFits;
}
