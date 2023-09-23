package kz.btsd.edmarket.online.module.service;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.online.model.Section;
import kz.btsd.edmarket.online.model.SectionFullDto;
import kz.btsd.edmarket.online.model.SubsectionDto;
import kz.btsd.edmarket.online.module.model.Module;
import kz.btsd.edmarket.online.module.model.ModuleConverter;
import kz.btsd.edmarket.online.module.model.ModuleDto;
import kz.btsd.edmarket.online.module.repository.ModuleRepository;
import kz.btsd.edmarket.online.progress.EventProgress;
import kz.btsd.edmarket.online.repository.SectionRepository;
import kz.btsd.edmarket.online.service.SectionService;
import kz.btsd.edmarket.online.service.SubsectionService;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserRole;
import kz.btsd.edmarket.view.repository.SubsectionUniqueViewRepository;
import kz.btsd.edmarket.view.repository.SubsectionViewRepository;
import kz.btsd.edmarket.view.service.SubsectionViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModuleService {
    @Autowired
    private SectionService sectionService;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private ModuleConverter moduleConverter;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private SubsectionService subsectionService;
    @Autowired
    private SubsectionViewService subsectionViewService;
    @Autowired
    private SubsectionUniqueViewRepository subsectionUniqueViewRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private EventRepository eventRepository;


    public List<Section> findAllByModuleId(Long id) {
        List<Long> ids = sectionRepository.findIdsByModuleIdOrderByPosition(id);
        List<Section> sections = new ArrayList<>();
        for (Long sectionId : ids) {
            sections.add(sectionService.findByIdWithLazy(sectionId));
        }
        return sections;
    }

    public List<ModuleDto> findAllByEventId(Long id) {
        List<Long> ids = moduleRepository.findIdsByEventIdOrderByPosition(id);
        List<ModuleDto> modules = new ArrayList<>();
        for (Long moduleId : ids) {
            ModuleDto moduleDto = findById(moduleId);
            modules.add(moduleDto);
        }
        return modules;
    }

    public void fillModuleFullDtos(List<ModuleDto> list, Long userId, EventProgress eventProgress) {
        for (ModuleDto moduleDto : list) {
            sectionService.fillSectionFullDto(moduleDto.getSections(), eventProgress);
            fillUserLikeAndRating(moduleDto, userId);
            fillFileNames(moduleDto);
            fillSubViewsForAdmin(moduleDto); // только для админа
        }
    }

    public ModuleDto findById(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        module.setSections(findAllByModuleId(id));
        ModuleDto moduleDto = moduleConverter.convertToDto(module);
        fillFileNames(moduleDto);
        return moduleDto;
    }

    public void fillUserLikeAndRating(ModuleDto moduleDto, Long userId) {
        if (userId != null) {
            for (SectionFullDto sectionFullDto : moduleDto.getSections()) {
                sectionService.fillMyLessonRating(sectionFullDto, userId);
                for (SubsectionDto subsectionDto :
                        sectionFullDto.getSubsections()) {
                    subsectionService.fillMyLike(subsectionDto, userId);
                }
            }
        }
    }

    public void fillFileNames(ModuleDto moduleDto) {
        for (SectionFullDto sectionFullDto : moduleDto.getSections()) {
            for (SubsectionDto subsectionDto :
                    sectionFullDto.getSubsections()) {
                subsectionService.fillFileNames(subsectionDto);
            }
        }
    }

    public void fillSubViewsForAdmin(ModuleDto moduleDto) {
        for (SectionFullDto sectionFullDto : moduleDto.getSections()) {
            for (SubsectionDto subsectionDto :
                    sectionFullDto.getSubsections()) {
                subsectionDto.setViews(subsectionViewService.findValueBySubsectionId(subsectionDto.getId()));
                subsectionDto.setUniqueViews(subsectionUniqueViewRepository.countBySubsectionId(subsectionDto.getId()));
            }
        }
    }

    public void calculateModule(Module module) {
        for (int i = 0; i < module.getSections().size(); i++) {
            module.getSections().get(i).setPosition((long) i);
            sectionService.calculateSection(module.getSections().get(i));
        }
    }

    private void removeEmptyUnit(Module module) {
        for (Section section :
                module.getSections()) {
            sectionService.removeEmptyUnit(section);
        }
    }

    public Module save(Module module) {
        removeEmptyUnit(module);
        calculateModule(module);
        module = moduleRepository.save(module);
        eventRepository.updateSubsectionsSize(module.getEventId()); //todo можно сделать асинхронно через события
        return module;
    }

    public ModuleDto save(ModuleDto moduleDto) {
        Module module = moduleConverter.convertToEntity(moduleDto);
        module = save(module);
        return findById(module.getId());
    }

    public void delete(Long id) {
        moduleRepository.deleteById(id);
    }

    public void checkModuleOwner(String userId, Long moduleId) {
        Module module = moduleRepository.findById(moduleId).get();
        eventService.checkEventOwner(userId, module.getEventId());
    }
}
