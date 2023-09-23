package kz.btsd.edmarket.user.controller.search;

import elastic.EventDto;
import kz.btsd.edmarket.event.model.CommentRating;
import kz.btsd.edmarket.online.module.model.ModuleDto;
import kz.btsd.edmarket.user.model.UserShortDto;
import lombok.Data;

import java.util.List;

@Data
public class UsersResponse {
    private EventDto event;
    private CommentRating commentRating;
    private List<UserShortDto> users;
    private boolean signed = false;
}
