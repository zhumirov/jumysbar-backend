package kz.btsd.edmarket.mobile.controller;

import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.mobile.model.*;
import kz.btsd.edmarket.mobile.model.enums.CourseFilter;
import kz.btsd.edmarket.mobile.service.MobileCourseService;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@AllArgsConstructor
@CrossOrigin(origins = "*")
@RestController
public class MobileDashboardController {

    private final UserConverter userConverter;
    private final UserRepository userRepository;
    private final MobileCourseService mobileCourseService;


    @GetMapping("/mobile/dashboard")
    public MobileDashboardDto getDashboard(@RequestParam(defaultValue = "all", required = false) CourseFilter filter,
                                           @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                           @RequestParam(defaultValue = "desc", required = false) String order) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MinimalUserDto minimalUserDto = null;
        Platform platform = Platform.JUMYSBAR;

        if (principal instanceof Jwt) {
            String phone = (String) ((Jwt) principal).getClaims().get("sub");
            Optional<User> userOptional = userRepository.findByPhoneAndDeletedFalse(phone);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                platform = user.getPlatform();
                minimalUserDto = userConverter.convertToMinimalDto(user);
            }
        }

        List<MinimalCourseDto> courses;

        if (CourseFilter.my.equals(filter) && minimalUserDto != null) {
            courses = mobileCourseService.findUserCourses(minimalUserDto.getId());
        } else {
            courses = mobileCourseService.findAll(platform, SortUtils.buildSort(sort, order));
        }

        return new MobileDashboardDto()
                .setUser(minimalUserDto)
                .setCourses(courses);
    }
}
