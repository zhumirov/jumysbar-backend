package kz.btsd.edmarket.invitation.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InvitationDto {
    private Long eventId;
    private String value;

    public InvitationDto() {
    }

    public InvitationDto(Long eventId, String value) {
        this.eventId = eventId;
        this.value = value;
    }
}
