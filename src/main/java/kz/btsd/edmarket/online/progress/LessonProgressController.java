package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class LessonProgressController {
    @Autowired
    private LessonProgressRepository lessonProgressRepository;
    @Autowired
    private LessonProgressConverter lessonProgressConverter;

    @GetMapping(value = "/lesson-progress/{id}")
    public LessonProgressDto findById(@PathVariable Long id) {
        LessonProgress lessonProgress = lessonProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        return lessonProgressConverter.convertToDto(lessonProgress);
    }

    @GetMapping(value = "/lesson-progress/{id}/test")
    public LessonProgress findBy(@PathVariable Long id) {
        return lessonProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    @GetMapping(value = "/lesson-progress/{id}/event")
    public List<LessonProgress> findByEvent(@PathVariable Long id) {
        return lessonProgressRepository.findByEventId(id);
    }
}
