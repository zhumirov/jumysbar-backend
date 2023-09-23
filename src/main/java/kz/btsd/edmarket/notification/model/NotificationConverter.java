package kz.btsd.edmarket.notification.model;

import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationConverter {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private ModelMapper modelMapper;

    public NotificationDto convertToDto(Notification notification) {
        NotificationDto notificationDto = modelMapper.map(notification, NotificationDto.class);
        notificationDto.setSender(userConverter.convertToShortDto(userRepository.findById(notificationDto.getSenderId()).get()));
        return notificationDto;
    }
}
