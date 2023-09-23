package kz.btsd.edmarket.online.progress;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kz.btsd.edmarket.online.module.model.ModuleProgressDto;
import kz.btsd.edmarket.user.model.UserShortDto;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Прогресс по полному курсу
 */
@Data
public class EventProgressFullStatsDto {
    private Long id;
    private UserShortDto user;
    private Long eventId;
    private String review;

    private double testSize;
    private double resolvedTestSize;
    private int homeworkSize;
    private int resolvedHomeworkSize;

    @JsonIgnore
    //список тестов
    private Set<String> sections;
    @JsonIgnore
    //список сабсекций
    private Set<Long> subsections;
    //прогресс по курсу
    private List<ModuleProgressDto> modules;

    public EventProgressFullStatsDto() {
    }
}
