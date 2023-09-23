package kz.btsd.edmarket.comment.repository;

import kz.btsd.edmarket.comment.like.model.CommentLike;
import kz.btsd.edmarket.comment.like.repository.CommentLikeRepository;
import kz.btsd.edmarket.comment.model.Comment;
import kz.btsd.edmarket.comment.model.CommentConverter;
import kz.btsd.edmarket.comment.model.CommentDto;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.event.repository.EventRepository;
import kz.btsd.edmarket.file.repository.FileRepository;
import kz.btsd.edmarket.mentor.repository.MentorRepository;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    public static final int THREAD_SIZE = 100;
    @Autowired
    private CommentConverter commentConverter;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private MentorRepository mentorRepository;

    public void valid(Comment comment) {
        if (comment.getStepId() == null && comment.getLessonId() == null) {
            throw new IllegalStateException("заполните stepId или lessonId");
        }
        if (comment.getThreadId() != null) {
            Comment commentThread = commentRepository.findById(comment.getThreadId()).get();
            if (commentThread.getThreadId() != null && !commentThread.getThreadId().equals(comment.getThreadId())) {
                throw new IllegalStateException("нельзя ответить во вложенный thread, комментарий уже находится в thread=" + commentThread.getThreadId());
            }
            if (comment.getAnswerToId() == null) {
                throw new IllegalStateException("заполните answerToId");
            }
        }
    }

    public CommentDto fillUserInfoAndConvert(Comment comment, Event event) {
        CommentDto commentDto = commentConverter.convertToDto(comment);
        fillUser(commentDto, event);
        fillAnswerTo(commentDto, event);
        fillFileInfo(commentDto);
        return commentDto;
    }

    private void fillAnswerTo(CommentDto commentDto, Event event) {
        if (commentDto.getAnswerToId() != null) {
            CommentDto answerTo = commentConverter.convertToDto(commentRepository.findById(commentDto.getAnswerToId()).get());
            User answerToUser = userRepository.findById(answerTo.getUserId()).get();
            answerTo.setUser(userConverter.convertToUCommentDto(answerToUser, event));
            commentDto.setAnswerTo(answerTo);
        }
    }

    private void fillUser(CommentDto commentDto, Event event) {
        User user = userRepository.findById(commentDto.getUserId()).get();
        commentDto.setUser(userConverter.convertToUCommentDto(user, event));
    }

    private void fillFileInfo(CommentDto commentDto) {
        commentDto.setFileInfos(new HashSet<>());
        for (String fileId :
                commentDto.getFiles()) {
            commentDto.getFileInfos().add(fileRepository.findDtoById(fileId));
        }
    }

    private void fillMyLike(CommentDto commentDto, Long userId) {
        if (userId != null) {
            Optional<CommentLike> optinal = commentLikeRepository.findByUserIdAndCommentId(userId, commentDto.getId());
            if (optinal.isPresent()) {
                commentDto.setCurrentUserCommentLike(optinal.get());
            }
        }
    }

    private void fillAdditionalVar(CommentDto commentDto, Long userId, Event event) {
        fillUser(commentDto, event);
        fillAnswerTo(commentDto, event);
        fillFileInfo(commentDto);
        if (userId != null) {
            fillMyLike(commentDto, userId);
        }
    }

    public List<CommentDto> commentConvertToDto(List<Comment> comments, Long userId, Event event) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = commentConverter.convertToDto(comment);
            fillAdditionalVar(commentDto, userId, event);
            commentDtos.add(commentDto);
        }
        return commentDtos;
    }

    public boolean canViewAllHomeworks(long stepId, long userId) {
        User user = userRepository.findById(userId).get();
        Event event = eventRepository.findByStepId(stepId);
        return user.isAdmin() || event.getUserId().equals(userId) || mentorRepository.existsByUserIdAndEventId(userId, event.getId());
    }

    public List<CommentDto> commentAddThreadAndConvertToDto(List<Comment> comments, Long userId, Event event) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = commentConverter.convertToDto(comment);
            List<Comment> answers = commentRepository.findByThreadIdOrderByCreatedDate(comment.getId(), PageRequest.of(0, THREAD_SIZE));
            List<CommentDto> answerDtos = new ArrayList<>();
            for (Comment ans :
                    answers) {
                CommentDto ansDto = commentConverter.convertToDto(ans);
                fillAdditionalVar(ansDto, userId, event);
                answerDtos.add(ansDto);
            }
            commentDto.setAnswers(answerDtos);
            fillAdditionalVar(commentDto, userId, event);
            commentDtos.add(commentDto);
        }
        return commentDtos;
    }


    public Event getEvent(Comment comment) {
        Event event = null;
        if (comment.getStepId() != null) {
            event = eventRepository.findByStepId(comment.getStepId());
        } else {
            event = eventRepository.findByLessonId(comment.getLessonId());

        }
        return event;
    }
}
