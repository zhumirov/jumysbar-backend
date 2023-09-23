package kz.btsd.edmarket.user.controller.search;

import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventConverter;
import kz.btsd.edmarket.event.model.EventTitleDto;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.online.progress.EventProgressRepository;
import kz.btsd.edmarket.subscription.model.Subscription;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.model.UserDto;
import kz.btsd.edmarket.user.repository.UserRepository;
import kz.btsd.edmarket.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class UserSearchController {
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private UserSearchService userSearchService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventProgressRepository eventProgressRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<UserDto> allByPlatformAndRole(Authentication authentication,
                                              @RequestParam(required = false) String query,
                                              @RequestParam(defaultValue = "JUMYSBAR", required = false) Platform platform,
                                              @RequestParam(defaultValue = "0", required = false) Integer from,
                                              @RequestParam(defaultValue = "20", required = false) Integer size,
                                              @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                              @RequestParam(defaultValue = "asc", required = false) String order) {
        User admin = userService.findById(authentication.getName());
        if (!(admin.isAdmin() || admin.isORG())) {
            throw new AuthorizationServiceException("только ADMIN может изменить");
        }
        if (StringUtils.isBlank(query)) { // для like-поиска делаем null
            query = null;
        } else {
            query = StringUtils.lowerCase(query); // для like поиска делаем lower
        }
        List<User> users = userRepository.findByPlatform(platform, query, PageRequest.of(from, size, SortUtils.buildSort(sort, order)));
        return users
                .stream()
                .map(userConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/stats")
    public UserStatsResponse allByPlatformStats(Authentication authentication,
                                                @RequestParam(required = false) String query,
                                                @RequestParam(defaultValue = "JUMYSBAR", required = false) Platform platform,
                                                @RequestParam(defaultValue = "0", required = false) Integer from,
                                                @RequestParam(defaultValue = "20", required = false) Integer size,
                                                @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                                @RequestParam(defaultValue = "asc", required = false) String order) {
        List<UserStatDto> userDtos = allByPlatformAndRole(authentication, query, platform, from, size, sort, order)
                                .stream()
                                .map(userConverter::convertToDto)
                                .collect(Collectors.toList());
        for (UserStatDto user :
                userDtos) {
            user.setEvents(eventRepository.findAllSubscribedEventdByUserId(user.getId()));
            Integer sumViewed = eventProgressRepository.sumViewedSubsectionsSizeByUserId(user.getId());
            user.setViewedSubsectionsSize(sumViewed != null ? sumViewed : 0);
        }
        UserStatsResponse userStatsResponse = new UserStatsResponse();
        userStatsResponse.setUsers(userDtos);
        userStatsResponse.setTotalUsers(userRepository.countByPlatform(platform, query));
        userStatsResponse.setNewUsers(userSearchService.countNewUsersForlast7(platform));
        userStatsResponse.setPopularEvents(userSearchService.top3SubscribedEvents(platform));
        userStatsResponse.setTopActiveUsers(userSearchService.top3ActiveUsers(platform));
        Integer sumViewedSubsectionsSizeByPlatform = eventProgressRepository.sumViewedSubsectionsSizeByPlatform(platform);
        userStatsResponse.setCountViewedSteps(sumViewedSubsectionsSizeByPlatform != null ? sumViewedSubsectionsSizeByPlatform : 0);

        return userStatsResponse;
    }

    @GetMapping(value = "/users/stats/files")
    public ResponseEntity<byte[]> allUsersByPlatform(Authentication authentication,
                                                     @RequestParam(required = false) String query,
                                                     @RequestParam(defaultValue = "JUMYSBAR", required = false) Platform platform,
                                                     @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                                     @RequestParam(defaultValue = "asc", required = false) String order) {
        int size = 1000;
        int maxFrom = 10000000;
        List<UserDto> userDtos = new ArrayList<>();
        for (int i = 0; i < maxFrom; i+=size) {
            List<UserDto> userParts = allByPlatformAndRole(authentication, query, platform, i, size, sort, order);
            if (userParts.size() == 0) {
                break;
            }
            userDtos.addAll(userParts);
        }
        List<UserStatDto> users = userDtos
                .stream()
                .map(userConverter::convertToDto)
                .collect(Collectors.toList());
        ByteArrayOutputStream stream = userSearchService.createCVS(users);
        String fileName = "users" + platform + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentLength(stream.size()) //
                .body(stream.toByteArray());
    }
}
