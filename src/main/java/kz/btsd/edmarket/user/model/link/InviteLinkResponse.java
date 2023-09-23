package kz.btsd.edmarket.user.model.link;

import kz.btsd.edmarket.event.model.EventTitleDto;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Date;

@Data
public class InviteLinkResponse {
    private InviteLink inviteLink;
    private EventTitleDto event;

    public InviteLinkResponse(InviteLink inviteLink, EventTitleDto event) {
        this.inviteLink = inviteLink;
        this.event = event;
    }
}
