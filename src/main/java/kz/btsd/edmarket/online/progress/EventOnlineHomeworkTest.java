package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.SectionProgressDto;
import kz.btsd.edmarket.online.model.SubsectionDto;
import kz.btsd.edmarket.online.model.SubsectionProgressDto;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class EventOnlineHomeworkTest {
    private List<SectionFullDto> exams = new LinkedList<>();
    private List<SubsectionDto> homeworks = new LinkedList<>();
    private Map<Long, Long> modules = new HashMap<>();

    public EventOnlineHomeworkTest(List<SectionFullDto> exams, List<SubsectionDto> homeworks, Map<Long, Long> modules) {
        this.exams = exams;
        this.homeworks = homeworks;
        this.modules = modules;
    }
}
