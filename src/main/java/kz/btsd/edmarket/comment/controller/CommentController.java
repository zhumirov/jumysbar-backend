package kz.btsd.edmarket.comment.controller;


import kz.btsd.edmarket.comment.like.repository.CommentLikeRepository;
import kz.btsd.edmarket.comment.model.Comment;
import kz.btsd.edmarket.comment.model.CommentDeleteDto;
import kz.btsd.edmarket.comment.model.CommentDto;
import kz.btsd.edmarket.comment.model.CommentGradeDto;
import kz.btsd.edmarket.comment.model.CommentStatus;
import kz.btsd.edmarket.comment.model.SearchResultDto;
import kz.btsd.edmarket.comment.repository.CommentRepository;
import kz.btsd.edmarket.comment.repository.CommentService;
import kz.btsd.edmarket.common.controller.utils.SortUtils;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.model.EventStatus;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.notification.service.CommentCreatedEvent;
import kz.btsd.edmarket.online.service.SubsectionService;
import kz.btsd.edmarket.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private SubsectionService subsectionService;
    @Autowired
    private AuthService authService;


    @GetMapping("/comments/{id}")
    public CommentDto findById(@PathVariable Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
        CommentDto commentDto = commentService.fillUserInfoAndConvert(comment, commentService.getEvent(comment));
        return commentDto;
    }

    @PostMapping("/comments")
    public CommentDto save(Authentication authentication, @Valid @RequestBody Comment comment) {
        authService.checkOwner(authentication.getName(), comment.getUserId());
        commentService.valid(comment);
        comment = commentRepository.save(comment);
        publisher.publishEvent(new CommentCreatedEvent(this, comment.getUserId(), comment));
        return findById(comment.getId());
    }

    @PutMapping("/comments/{id}")
    public CommentDto changeEvent(Authentication authentication, @RequestBody Comment comment, @PathVariable Long id) {
        authService.checkOwner(authentication.getName(), comment.getUserId());
        commentService.valid(comment);
        comment.setLastModifiedDate(new Date()); //todo переделать на аннотацию @LastModifiedDate
        comment.setStatus(CommentStatus.EDITED);
        comment = commentRepository.save(comment);
        return findById(comment.getId());
    }

    //комментарии для шага
    @GetMapping(value = "/comments")
    public SearchResultDto<CommentDto> searchByStepId(@RequestParam Long stepId,
                                                      @RequestParam(required = false) Long currentUserId,
                                                      @RequestParam(defaultValue = "false", required = false) Boolean homework,
                                                      @RequestParam(defaultValue = "0", required = false) Integer page,
                                                      @RequestParam(defaultValue = "20", required = false) Integer size,
                                                      @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                                      @RequestParam(defaultValue = "asc", required = false) String order) {

        long homeworkTotalHits;
        if (subsectionService.hiddenHomework(stepId) && !commentService.canViewAllHomeworks(stepId, currentUserId)) {
            homeworkTotalHits = commentRepository.countByStepIdAndHomeworkAndUserIdAndThreadIdIsNull(stepId, true, currentUserId);
        } else {
            homeworkTotalHits=commentRepository.countByStepIdAndHomeworkIsTrue(stepId);
        }
        List<Comment> comments;
        if (homework && subsectionService.hiddenHomework(stepId) && !commentService.canViewAllHomeworks(stepId, currentUserId)) {
            comments = commentRepository.findByStepIdAndHomeworkAndUserIdAndThreadIdIsNull(stepId, homework, currentUserId, PageRequest.of(page, size, SortUtils.buildSort(sort, order)));
        } else {
            comments = commentRepository.findByStepIdAndHomeworkAndThreadIdIsNull(stepId, homework, PageRequest.of(page, size, SortUtils.buildSort(sort, order)));
        }
        List<CommentDto> commentDtos = commentService.commentAddThreadAndConvertToDto(comments, currentUserId, eventRepository.findByStepId(stepId));
        return new SearchResultDto<>(commentDtos, commentRepository.countCommentWithOutHomeworkTab(stepId), homeworkTotalHits);
    }

    //комментарии для урока
    @GetMapping(value = "/comments/lessons")
    public SearchResultDto<CommentDto> searchByLessonId(@RequestParam Long lessonId,
                                                        @RequestParam(required = false) Long currentUserId,
                                                        @RequestParam(defaultValue = "0", required = false) Integer page,
                                                        @RequestParam(defaultValue = "20", required = false) Integer size) {
        List<Comment> comments = commentRepository.findByLessonIdAndThreadIdIsNullOrderByCreatedDate(lessonId, PageRequest.of(page, size));
        long totalHits = commentRepository.countByLessonId(lessonId);
        List<CommentDto> commentDtos = commentService.commentAddThreadAndConvertToDto(comments, currentUserId, eventRepository.findByLessonId(lessonId));
        return new SearchResultDto<>(commentDtos, totalHits);
    }

    //все отзывы курса по урокам
    @GetMapping(value = "/comments/lessons-events")
    public SearchResultDto<CommentDto> searchAllLessonsByEventId(@RequestParam Long eventId,
                                                        @RequestParam(required = false) Long currentUserId,
                                                        @RequestParam(defaultValue = "0", required = false) Integer page,
                                                        @RequestParam(defaultValue = "20", required = false) Integer size) {
        Event event = eventRepository.findById(eventId).get();
        if (event.getStatus().equals(EventStatus.DRAFT)) {
            eventId = eventRepository.findByParentId(eventId).get().getId();
        }
        List<Comment> comments = commentRepository.findLessonsByEventIdOrderByCreatedDate(eventId, PageRequest.of(page, size));
        long totalHits = commentRepository.countLessonsByEventId(eventId);
        List<CommentDto> commentDtos = commentService.commentAddThreadAndConvertToDto(comments, currentUserId, eventRepository.findById(eventId).get());
        return new SearchResultDto<>(commentDtos, totalHits);
    }

    //  комментариев пользователя в thread, предпологается что по умолчанию сред свернут и достаешь по нажатию на догрузить
    @GetMapping(value = "/comments/threads")
    public List<CommentDto> searchByThreadId(@RequestParam Long threadId,
                                             @RequestParam(required = false) Long currentUserId,
                                             @RequestParam(defaultValue = "0", required = false) Integer page,
                                             @RequestParam(defaultValue = "20", required = false) Integer size) {
        List<Comment> comments = commentRepository.findByThreadIdOrderByCreatedDate(threadId, PageRequest.of(page, size));
        List<CommentDto> commentDtos = commentService.commentConvertToDto(comments, currentUserId, commentService.getEvent(comments.get(0)));
        return commentDtos;
    }

    // комментарии пользователя
    @GetMapping(value = "/comments/users")
    public List<CommentDto> searchByUserId(@RequestParam Long userId,
                                           @RequestParam(required = false) Long currentUserId,
                                           @RequestParam(defaultValue = "0", required = false) Integer page,
                                           @RequestParam(defaultValue = "false", required = false) Boolean homework,
                                           @RequestParam(defaultValue = "20", required = false) Integer size,
                                           @RequestParam(defaultValue = "createdDate", required = false) String sort,
                                           @RequestParam(defaultValue = "asc", required = false) String order) {
        List<Comment> comments = commentRepository.findByUserIdAndHomeworkAndThreadIdIsNull(userId, homework, PageRequest.of(page, size, SortUtils.buildSort(sort, order)));
        List<CommentDto> commentDtos = new ArrayList<>();
        if (comments.size() > 0) {
            commentDtos = commentService.commentAddThreadAndConvertToDto(comments, currentUserId, commentService.getEvent(comments.get(0)));
        }
        return commentDtos;
    }

    @PostMapping(value = "/comments/grade")
    public ResponseEntity<?> gradeHomework(Authentication authentication, @RequestBody CommentGradeDto dto) {
        Comment comment = commentRepository.findById(dto.getId()).get();
        authService.checkOwner(authentication.getName(), comment.getUserId());
        comment.setGrade(dto.getGrade());
        commentRepository.save(comment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{id}")
    public CommentDeleteDto delete(Authentication authentication, @PathVariable Long id) {
        Comment comment = commentRepository.findById(id).get();
        authService.checkOwner(authentication.getName(), comment.getUserId());
        if (commentRepository.countByAnswerToId(id) > 0) {
            comment.setStatus(CommentStatus.DELETED);
            commentRepository.save(comment);
            return new CommentDeleteDto(CommentStatus.DELETED, findById(id));
        } else {
            commentLikeRepository.deleteByCommentId(id);
            commentRepository.deleteById(id);
            return new CommentDeleteDto(CommentStatus.FULL_DELETED);
        }
    }
}
