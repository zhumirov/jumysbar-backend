package kz.btsd.edmarket.online.module.service;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.elastic.EventElasticService;
import kz.btsd.edmarket.event.model.EntityStatus;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventConverter;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.moderation.EventModerationRepository;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.repository.PlanRepository;
import kz.btsd.edmarket.online.model.Section;
import kz.btsd.edmarket.online.model.Subsection;
import kz.btsd.edmarket.online.module.model.Module;
import kz.btsd.edmarket.online.module.model.ModuleConverter;
import kz.btsd.edmarket.online.module.repository.ModuleRepository;
import kz.btsd.edmarket.review.repository.ReviewRepository;
import kz.btsd.edmarket.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ModuleBasketService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventConverter eventConverter;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private EventElasticService eventElasticService;
    @Autowired
    private EventModerationRepository eventModerationRepository;
    @Autowired
    private ModuleConverter moduleConverter;
    @Autowired
    private UserService userService;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private PlanRepository planRepository;

    private Section emptySection(Long id) {
        return new Section(id, new ArrayList<>());
    }

    private Module emptyModule(Module module) {
        module.getSections().clear();
        return module;
    }

    public void deleteModule(Long id) {
        Module savedModule = moduleConverter.convertToEntity(moduleService.findById(id));
        Module newModule = moduleConverter.convertToEntity(moduleService.findById(id));
        newModule.setStatus(EntityStatus.DELETED);
        calculateAndAddOldSections(emptyModule(newModule), savedModule);
        moduleService.save(newModule);
    }

    private void calculateAndAddOldSections(Module newModule, Module savedModule) {
        if (newModule.getId() == null) {
            return;
        }
        Map<Long, Section> savedSectionMap = new HashMap<>();
        for (Section section :
                savedModule.getSections()) {
            savedSectionMap.put(section.getId(), section);
        }
        for (Section section : newModule.getSections()) {
            if (section.getId() != null) {
                savedSectionMap.remove(section.getId());
                calculateAndAddOldSubsections(section, savedSectionMap.get(section.getId()));
            }
        }
        for (Long sectionId : savedSectionMap.keySet()) {
            Section section = savedSectionMap.get(sectionId);
            section.setStatus(EntityStatus.DELETED);
            calculateAndAddOldSubsections(emptySection(sectionId), section); // сделать Deleted и все subsections
            newModule.getSections().add(section);
        }
    }


    private void calculateAndAddOldSubsections(Section newSection, Section savedSection) {
        if (newSection.getId() == null) {
            return;
        }
        Map<Long, Subsection> savedSubsectionMap = new HashMap<>();
        for (Subsection subsection :
                savedSection.getSubsections()) {
            savedSubsectionMap.put(subsection.getId(), subsection);
        }
        for (Subsection subsection : newSection.getSubsections()) {
            if (subsection.getId() != null) {
                savedSubsectionMap.remove(subsection.getId());
            }
        }
        for (Long sectionId : savedSubsectionMap.keySet()) {
            Subsection subsection = savedSubsectionMap.get(sectionId);
            subsection.setStatus(EntityStatus.DELETED);
            newSection.getSubsections().add(subsection);
        }
    }

    public void restoreModule(Long id) {
    }

}
