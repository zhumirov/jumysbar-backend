package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.SubsectionDto;
import kz.btsd.edmarket.online.service.SectionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Service
public class LessonProgressConverter {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SectionService sectionService;

    public LessonProgressDto convertToDto(LessonProgress lessonProgress) {
        LessonProgressDto lessonProgressDto = modelMapper.map(lessonProgress, LessonProgressDto.class);
        lessonProgressDto.setSubsections(new LinkedList<>());
        SectionFullDto section = sectionService.findById(lessonProgress.getLessonId());
        Map<Long, SubsectionDto> subsectionDtoMap = new HashMap<>();
        for (SubsectionDto subsectionDto :
                section.getSubsections()) {
            subsectionDtoMap.put(subsectionDto.getId(), subsectionDto);
        }
        if (lessonProgress.getSubsections() != null) {
            for (Long subsecitonId :
                    lessonProgress.getSubsections()) {
                lessonProgressDto.getSubsections().add(subsectionDtoMap.get(subsecitonId));
            }
        }
        return lessonProgressDto;
    }

}
