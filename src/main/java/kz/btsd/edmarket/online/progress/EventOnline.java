package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.SubsectionDto;
import kz.btsd.edmarket.online.module.model.ModuleDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EventOnline {
    private List<ModuleDto> modules;
    private Map<Long, SectionFullDto> sections;
    private Map<Long, SubsectionDto> subsections;

    public EventOnline(List<ModuleDto> modules, Map<Long, SectionFullDto> sections, Map<Long, SubsectionDto> subsections) {
        this.modules = modules;
        this.sections = sections;
        this.subsections = subsections;
    }
}
