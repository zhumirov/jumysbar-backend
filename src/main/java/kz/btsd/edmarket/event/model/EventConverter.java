package kz.btsd.edmarket.event.model;

import elastic.EventDto;
import kz.btsd.edmarket.event.promocode.Promocode;
import kz.btsd.edmarket.mobile.model.MinimalCourseDto;
import kz.btsd.edmarket.user.model.Platform;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserConverter;
import kz.btsd.edmarket.user.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EventConverter {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private ModelMapper modelMapper;

    public Event cloneWithOutId(EventDto event) {
        Event clone = modelMapper.map(event, Event.class);
        clone.setId(null);
        clone.setTitle("КОПИЯ "+clone.getTitle());
        if (clone.getPromoCodes() != null) {
            for (Promocode promocode :
                    clone.getPromoCodes()) {
                promocode.setId(null);
                promocode.setEventId(null);
            }
        }
        if (clone.getPlans() != null) {
            for (Plan plan :
                    clone.getPlans()) {
                plan.setId(null);
                plan.setEventId(null);
            }
        }
        return clone;
    }

    public EventDto convertToDto(Event event) {
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        User user = userRepository.findById(event.getUserId()).get();
        eventDto.setUser(userConverter.convertToDto(user));
        return eventDto;
    }

    public MinimalCourseDto convertToMinimalDto(Event event) {
        MinimalCourseDto dto = new MinimalCourseDto()
                .setId(event.getId())
                .setTitle(event.getTitle())
                .setLogoId(event.getFileId());
        if (Objects.nonNull(event.getUserId())) {
            userRepository.findById(event.getUserId())
                    .ifPresent(user -> dto.setAuthor(getFullName(user)));
        }
        return dto;
    }

    public String getFullName(User user) {
        if (StringUtils.isNotBlank(user.getFirstName())
                && StringUtils.isNotBlank(user.getLastName())) {
            return user.getLastName() + " " + user.getLastName();
        }
        return user.getName();
    }
}
