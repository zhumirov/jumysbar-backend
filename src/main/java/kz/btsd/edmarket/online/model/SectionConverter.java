package kz.btsd.edmarket.online.model;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class SectionConverter {
    @Autowired
    private SubsectionConverter subsectionConverter;
    @Autowired
    private ModelMapper modelMapper;

    public SectionFullDto convertToFullDto(Section section) {
        SectionFullDto sectionFullDto = modelMapper.map(section, SectionFullDto.class);
        sectionFullDto.setSubsections(section.getSubsections().stream().map(subsection -> subsectionConverter.convertToDto(subsection)).collect(Collectors.toList()));
        return sectionFullDto;
    }

    public Section cloneWithOutId(SectionFullDto section, Long eventId) {
        Section clone = modelMapper.map(section, Section.class);
        clone.setSubsections(section.getSubsections().stream().map(subsection -> subsectionConverter.cloneWithOutId(subsection)).collect(Collectors.toList()));
        clone.setId(null);
        clone.setEventId(eventId);
        clone.setModuleId(null);
        return clone;
    }

    public Section convertToEntity(SectionFullDto sectionFullDto) {
        Section section = modelMapper.map(sectionFullDto, Section.class);
        section.setSubsections(sectionFullDto.getSubsections().stream().map(subsectionDto -> subsectionConverter.convertToEntity(subsectionDto)).collect(Collectors.toList()));
        return section;
    }

    public SectionProgressDto convertToProgressDto(SectionFullDto sectionFullDto) {
        SectionProgressDto sectionProgressDto = modelMapper.map(sectionFullDto, SectionProgressDto.class);
        sectionProgressDto.setSubsections(sectionFullDto.getSubsections().stream().map(subsection -> subsectionConverter.convertToProgressDto(subsection)).collect(Collectors.toList()));
        return sectionProgressDto;
    }
}
