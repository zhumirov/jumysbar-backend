package kz.btsd.edmarket.online.module.controller;

import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.event.service.EventService;
import kz.btsd.edmarket.online.module.model.Module;
import kz.btsd.edmarket.online.module.model.ModuleDto;
import kz.btsd.edmarket.online.module.model.ModulePositionDto;
import kz.btsd.edmarket.online.module.model.ModulePositionRequest;
import kz.btsd.edmarket.online.module.repository.ModuleRepository;
import kz.btsd.edmarket.online.module.service.ModuleBasketService;
import kz.btsd.edmarket.online.module.service.ModuleService;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.service.AuthService;
import kz.btsd.edmarket.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
public class ModuleController {
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ModuleBasketService moduleBasketService;
    @Autowired
    private EventService eventService;

    /**
     * @param userId - id пользователя для которого загружаются информация о его лайках/дизлайках на уроки.
     * @return
     */
    @GetMapping("/modules/{id}")
    public ModuleDto findById(@PathVariable Long id,
                              @RequestParam(required = false) Long userId) {
        ModuleDto moduleDto = moduleService.findById(id);
        moduleService.fillUserLikeAndRating(moduleDto, userId);
        return moduleDto;
    }

    @PostMapping("/modules")
    public ModuleDto save(Authentication authentication, @Valid @RequestBody ModuleDto moduleDto) {
        eventService.checkEventOwner(authentication.getName(), moduleDto.getEventId());
        if (!eventRepository.findById(moduleDto.getEventId()).get().getStatus().equals(EventStatus.DRAFT)) {
            throw new IllegalStateException("версия не может быть изменена");
        }
        return moduleService.save(moduleDto);
    }

    @PutMapping("/modules/{id}")
    public ModuleDto changeEvent(Authentication authentication, @RequestBody ModuleDto moduleDto, @PathVariable Long id) {
        eventService.checkEventOwner(authentication.getName(), moduleDto.getEventId());
        if (!eventRepository.findById(moduleDto.getEventId()).get().getStatus().equals(EventStatus.DRAFT)) {
            throw new IllegalStateException("версия не может быть изменена");
        }
        return moduleService.save(moduleDto);

    }

    @PutMapping("/modules/position")
    public ResponseEntity<?> position(Authentication authentication, @RequestBody ModulePositionRequest request) {
        User owner = userService.findById(authentication.getName());
        for (ModulePositionDto dto :
                request.getModules()) {
            Module module = moduleRepository.findById(dto.getId()).get();
            if (!owner.getId().equals(module.getUserId())) {
                throw new AccessDeniedException("Только создатель может изменить");
            }
            moduleRepository.updatePosition(dto.getId(), dto.getPosition());
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/modules/{id}/restore")
    public ResponseEntity<?> restore(Authentication authentication, @PathVariable Long id) {
        moduleService.checkModuleOwner(authentication.getName(), id);
        moduleBasketService.restoreModule(id);
        return ResponseEntity.ok().build();
    }

    //todo продумать со статусами?
    @DeleteMapping("/modules/{id}")
    public void delete(Authentication authentication, @PathVariable Long id) {
        moduleService.checkModuleOwner(authentication.getName(), id);
        moduleBasketService.deleteModule(id);
    }

    @DeleteMapping("/modules/full-delete/{id}")
    public void fullDelete(Authentication authentication, @PathVariable Long id) {
        moduleService.checkModuleOwner(authentication.getName(), id);
        moduleService.delete(id);
        //eventBasketService.deleteEvent(id, EventStatus.FULL_DELETED);
    }
}
