package kz.btsd.edmarket.mobile.controller;

import kz.btsd.edmarket.mobile.model.LessonDto;
import kz.btsd.edmarket.mobile.service.MobileLessonService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class MobileLessonController {

    private final MobileLessonService mobileLessonService;

    @GetMapping("/mobile/lesson/{id}")
    public LessonDto getLesson(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable Long id) {
        String phone = jwt != null ? jwt.getSubject() : null;
        return mobileLessonService.getLesson(id, phone);
    }
}
