package kz.btsd.edmarket.user.model;

import kz.btsd.edmarket.comment.model.UserCommentDto;
import kz.btsd.edmarket.common.exceptions.EntityNotFoundException;
import kz.btsd.edmarket.event.model.Event;
import kz.btsd.edmarket.mentor.repository.MentorRepository;
import kz.btsd.edmarket.mobile.model.MinimalUserDto;
import kz.btsd.edmarket.user.controller.search.UserStatDto;
import kz.btsd.edmarket.user.model.erg.SignupEmployeeDto;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserConverter {
    @Autowired
    private UserRepository repository;
    @Autowired
    private MentorRepository mentorRepository;
    @Autowired
    private ModelMapper modelMapper;

    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public UserShortDto convertToShortDto(User user) {
        return modelMapper.map(user, UserShortDto.class);
    }

    public UserStatDto convertToDto(UserDto userDto) {
        return modelMapper.map(userDto, UserStatDto.class);
    }

    public User convertToEntity(SignupEmployeeDto signup, String password) {
        User user = modelMapper.map(signup, User.class);
        user.setPassword(password);
        user.setCreatedDate(new Date());
        return user;
    }

    public User convertToEntity(SignupEmailDto signup, String password) {
        User user = modelMapper.map(signup, User.class);
        user.setPassword(password);
        user.setCreatedDate(new Date());
        return user;
    }

    public UserCommentDto convertToUCommentDto(User user, Event event) {
        UserCommentDto userDto = modelMapper.map(user, UserCommentDto.class);
        if (mentorRepository.existsByPhoneAndEventId(user.getPhone(), event.getId())) {
            userDto.setEventRole(UserRole.MENTOR);
        } else {
            if (event.getUserId().equals(user.getId())) {
                userDto.setEventRole(UserRole.ORG);
            } else {
                userDto.setEventRole(UserRole.USER);

            }
        }
        return userDto;
    }

    public User convertToSavedEntity(UserAddOrgInfoDto userAddOrgInfoDto) {
        User user = repository.findById(userAddOrgInfoDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(userAddOrgInfoDto.getId()));
        modelMapper.map(userAddOrgInfoDto, user);
        return user;
    }

    public MinimalUserDto convertToMinimalDto(User user) {
        return MinimalUserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fileId(user.getFileId())
                .build();
    }
}
