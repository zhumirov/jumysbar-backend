package kz.btsd.edmarket.online.module.model;

import kz.btsd.edmarket.online.model.SectionConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ModuleConverter {
    @Autowired
    private SectionConverter sectionConverter;
    @Autowired
    private ModelMapper modelMapper;

    public ModuleDto convertToDto(Module module) {
        ModuleDto moduleDto = modelMapper.map(module, ModuleDto.class);
        moduleDto.setSections(module.getSections().stream().map(section -> sectionConverter.convertToFullDto(section)).collect(Collectors.toList()));

        return moduleDto;
    }

    public Module cloneWithEventId(ModuleDto module, Long eventId) {
        Module clone = modelMapper.map(module, Module.class);
        clone.setSections(module.getSections().stream().map(section -> sectionConverter.cloneWithOutId(section, eventId)).collect(Collectors.toList()));
        clone.setId(null);
        clone.setEventId(eventId);
        return clone;
    }

    public Module convertToEntity(ModuleDto moduleDto) {
        Module module = modelMapper.map(moduleDto, Module.class);
        module.setSections(moduleDto.getSections().stream().map(sectionFullDto -> sectionConverter.convertToEntity(sectionFullDto)).collect(Collectors.toList()));
        return module;
    }

    public ModuleProgressDto convertToProgressDto(ModuleDto moduleDto) {
        ModuleProgressDto moduleProgressDto = modelMapper.map(moduleDto, ModuleProgressDto.class);
        moduleProgressDto.setSections(moduleDto.getSections().stream().map(section -> sectionConverter.convertToProgressDto(section)).collect(Collectors.toList()));
        return moduleProgressDto;
    }
}
