package kz.btsd.edmarket.online.module.model;

import kz.btsd.edmarket.online.model.SectionConverter;
import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.SectionProgressDto;
import kz.btsd.edmarket.online.model.SubsectionDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.stream.Collectors;

@SpringBootTest
class ModuleConverterTest {
    @Autowired
    ModuleConverter moduleConverter;
    @Autowired
    SectionConverter sectionConverter;

    private ModuleDto generateModuleDto() {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setSections(new ArrayList<>());
        for (int i = 0; i < 100; i++) {
            SectionFullDto sectionFullDto = new SectionFullDto();
            sectionFullDto.setSubsections(new ArrayList<>());
            for (int j = 0; j < 100; j++) {
                SubsectionDto subsectionDto = new SubsectionDto();
                sectionFullDto.getSubsections().add(subsectionDto);
            }
            moduleDto.getSections().add(sectionFullDto);
        }
        return moduleDto;
    }

    public PropertyMap<ModuleDto, ModuleProgressDto> skipModifiedFieldsMap = new PropertyMap<ModuleDto, ModuleProgressDto>() {
        protected void configure() {
            skip().setSections(null);
        }
    };
    public PropertyMap<SectionFullDto, SectionProgressDto> skipSection = new PropertyMap<SectionFullDto, SectionProgressDto>() {
        protected void configure() {
            skip().setSubsections(null);
        }
    };

    @org.junit.jupiter.api.Test
    void convertToProgressDto() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(skipModifiedFieldsMap);
          modelMapper.addMappings(skipSection);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);


        for (int i = 0; i < 300; i++) {
            //  ModelMapper modelMapper = new ModelMapper();
            ModuleDto moduleDto = generateModuleDto();
            ModuleProgressDto moduleProgressDto = modelMapper.map(moduleDto, ModuleProgressDto.class);
            moduleProgressDto.setSections(moduleDto.getSections().stream().map(section -> sectionConverter.convertToProgressDto(section)).collect(Collectors.toList()));

        }

    }

    @org.junit.jupiter.api.Test
    void convertToProgressDtoAutho() {
        ModuleDto moduleDto = generateModuleDto();
        for (int i = 0; i < 300; i++) {
            //  ModelMapper modelMapper = new ModelMapper();
            moduleConverter.convertToProgressDto(moduleDto);
        }

    }
}
