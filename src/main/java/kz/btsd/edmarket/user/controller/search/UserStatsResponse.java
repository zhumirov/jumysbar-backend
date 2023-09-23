package kz.btsd.edmarket.user.controller.search;

import kz.btsd.edmarket.event.model.EventTitleDto;
import kz.btsd.edmarket.user.model.UserShortDto;
import lombok.Data;

import java.util.List;

@Data
public class UserStatsResponse {
    private long newUsers;
    private List<EventTitleDto> popularEvents;
    private long countViewedSteps;
    private List<UserShortDto> topActiveUsers;
    private List<UserStatDto> users;
    private long totalUsers;
}
