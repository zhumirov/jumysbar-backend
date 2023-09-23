package kz.btsd.edmarket.online.controller;

import kz.btsd.edmarket.online.model.SubsectionDto;
import kz.btsd.edmarket.online.model.UserActionDto;
import kz.btsd.edmarket.online.model.UserActionResultDto;
import kz.btsd.edmarket.online.service.SubsectionService;
import kz.btsd.edmarket.subscription.model.SubscriptionCreateRequest;
import kz.btsd.edmarket.subscription.service.SubscriptionService;
import kz.btsd.edmarket.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
public class SubsectionController {
    @Autowired
    private SubsectionService subsectionService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private AuthService authService;

    /**
     * @param userId - id пользователя для которого загружаются информация о его лайках/дизлайках на уроки.
     * @return
     */
    @GetMapping("/subsections/{id}")
    public SubsectionDto findById(@PathVariable Long id,
                                  @RequestParam(required = false) Long userId) {
        return subsectionService.findById(id, userId);
    }

    @PostMapping("/subsections/action")
    public UserActionResultDto save(Authentication authentication, @Valid @RequestBody UserActionDto userActionDto) {
        authService.checkOwner(authentication.getName(), userActionDto.getUserId());
        subscriptionService.checkAndCreateSubscriptionAsync(new SubscriptionCreateRequest(userActionDto.getUserId(), userActionDto.getEventId()));
        subsectionService.addToEventProgress(userActionDto.getEventId(), userActionDto.getUserId(), userActionDto.getId());
        return new UserActionResultDto(subsectionService.calcSectionProgress(userActionDto.getEventId(),
                userActionDto.getId(), userActionDto.getUserId()));
    }

    @GetMapping("/subsections/{stepId}/activity/{userId}")
    public UserLessonActivityDto userActivity(Authentication authentication, @PathVariable Long stepId, @PathVariable Long userId) {
        authService.checkOwner(authentication.getName(), userId);
        return subsectionService.checkP2p(stepId, userId);
    }
}
