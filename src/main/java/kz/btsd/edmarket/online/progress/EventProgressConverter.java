package kz.btsd.edmarket.online.progress;

import kz.btsd.edmarket.event.model.EventConverter;
import kz.btsd.edmarket.event.model.EventResponse;
import kz.btsd.edmarket.online.progress.testhomework.EventProgressUserRow;
import kz.btsd.edmarket.online.progress.testhomework.UserEventProgressDto;
import kz.btsd.edmarket.user.model.User;
import kz.btsd.edmarket.user.model.UserShortDto;
import kz.btsd.edmarket.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProgressConverter {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EventConverter eventConverter;

    public EventProgressFullStatsDto convertToFullDto(EventProgress eventProgress, User user) {
        EventProgressFullStatsDto eventProgressFullStatsDto = modelMapper.map(eventProgress, EventProgressFullStatsDto.class);
        UserShortDto userShortDto = modelMapper.map(user, UserShortDto.class);
        eventProgressFullStatsDto.setUser(userShortDto);
        return eventProgressFullStatsDto;
    }

    public EventProgressUserRow convertToTableDto(EventProgress eventProgress, User user) {
        EventProgressUserRow eventProgressFullStatsDto = modelMapper.map(eventProgress, EventProgressUserRow.class);
        eventProgressFullStatsDto.setStartDate(eventProgress.getStartDate());
        eventProgressFullStatsDto.setEndDate(eventProgress.getEndDate());
        UserEventProgressDto userDto = modelMapper.map(user, UserEventProgressDto.class);
        userDto.setName(eventConverter.getFullName(user));
        eventProgressFullStatsDto.setUser(userDto);
        return eventProgressFullStatsDto;
    }

}
