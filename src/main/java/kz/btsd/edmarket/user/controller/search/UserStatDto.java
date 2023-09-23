package kz.btsd.edmarket.user.controller.search;

import kz.btsd.edmarket.event.model.EventTitleDto;
import kz.btsd.edmarket.subscription.model.Subscription;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserStatDto {
    private Long id;
    private String name;
    private String phone;
    private String email;
    //БИН/ИИН - для ecommerce/mastercard
    private String identifier;
    //предприятие для ERG
    private String company;

    private Date createdDate;
    private Date lastActivityDate;

    private List<EventTitleDto> events;

    private int viewedSubsectionsSize = 0;

    public UserStatDto() {
    }
}
