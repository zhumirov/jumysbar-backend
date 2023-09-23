package kz.btsd.edmarket.mobile.controller;

import kz.btsd.edmarket.mobile.model.*;
import kz.btsd.edmarket.mobile.service.MobileCourseService;
import kz.btsd.edmarket.user.model.UserDto;
import kz.btsd.edmarket.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@AllArgsConstructor
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/mobile")
public class MobileCourseController {

    private final MobileCourseService mobileCourseService;
    private final UserService userService;

    @GetMapping("/course/{id}")
    public CourseDto getCourse(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        final Long userId = jwt != null ? userService.findById(jwt.getSubject()).getId() : null;
        return mobileCourseService.findById(id, userId);
    }

    @GetMapping("/course/subscribed")
    public List<MinimalCourseDto> getSubscribedCourses(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return Collections.emptyList();
        }
        UserDto userDto = userService.findByIdtoDto(jwt.getSubject());
        return mobileCourseService.findUserCourses(userDto.getId());
    }
}
