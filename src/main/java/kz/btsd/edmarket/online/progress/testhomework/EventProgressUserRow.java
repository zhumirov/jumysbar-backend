package kz.btsd.edmarket.online.progress.testhomework;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kz.btsd.edmarket.online.model.SectionProgressDto;
import kz.btsd.edmarket.online.model.SubsectionProgressDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Прогресс пользователя по полному курсу
 */
@Data
public class EventProgressUserRow {
    private Long id;
    private UserEventProgressDto user;
    private Long eventId;
    private String review;

    private double testSize;
    private double resolvedTestSize;
    private int homeworkSize;
    private int resolvedHomeworkSize;
    private double totalResult;
    private double resolvedTotalResult;
    private double totalPercent;
    private Long certificateId;
    private Long subscriptionId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @JsonIgnore
    //список сабсекций
    private Set<Long> sections;
    @JsonIgnore
    //список сабсекций
    private Set<Long> subsections;
    private List<SectionProgressDto> exams = new LinkedList<>();
    private List<SubsectionProgressDto> homeworks = new LinkedList<>();

    public EventProgressUserRow() {
    }
}
