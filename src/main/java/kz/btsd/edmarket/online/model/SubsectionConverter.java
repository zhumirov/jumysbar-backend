package kz.btsd.edmarket.online.model;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubsectionConverter {
    @Autowired
    private ModelMapper modelMapper;

    public SubsectionDto convertToDto(Subsection subsection) {
        SubsectionDto subsectionDto = modelMapper.map(subsection, SubsectionDto.class);
        return subsectionDto;
    }

    public Subsection cloneWithOutId(SubsectionDto subsection) {
        Subsection clone = modelMapper.map(subsection, Subsection.class);
        clone.setId(null);
        clone.setSectionId(null);
        return clone;
    }

    public SubsectionProgressDto convertToProgressDto(SubsectionDto subsectionDto) {
        SubsectionProgressDto subsectionProgressDto = modelMapper.map(subsectionDto, SubsectionProgressDto.class);
        return subsectionProgressDto;
    }

    public Subsection convertToEntity(SubsectionDto subsectionDto) {
        Subsection subsection = modelMapper.map(subsectionDto, Subsection.class);
        return subsection;
    }
}
