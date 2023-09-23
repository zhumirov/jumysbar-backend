package kz.btsd.edmarket.online.subscription;

import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.subscription.repository.SubscriptionRepository;
import kz.btsd.edmarket.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class OnlineSubscriptionController {
    @Autowired
    private SubscriptionRepository repository;
    @Autowired
    private AuthService authService;

    /**
     * метод возврашает все онлайн заявки пользователя
     */
    @GetMapping("/subscriptions/online/user")
    public List<OnlineSubscriptionDto> allByUserId(Authentication authentication,
                                                   @RequestParam Long createdUserId,
                                                   @RequestParam(defaultValue = "0", required = false) Integer from,
                                                   @RequestParam(defaultValue = "20", required = false) Integer size,
                                                   @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                                   @RequestParam(defaultValue = "asc", required = false) String order) {
        authService.checkOwner(authentication.getName(), createdUserId);
        List<OnlineSubscriptionDto> list = repository.findAllOnlineByUserId(createdUserId, PageRequest.of(from, size, SortUtils.buildSort(sort, order)));
        for (OnlineSubscriptionDto dto :
                list) {
            if (dto.getViewedSubsectionsSize() > dto.getSubsectionsSize()) {
                dto.setViewedSubsectionsSize(dto.getSubsectionsSize());
            }
        }
        return list;
    }
}
