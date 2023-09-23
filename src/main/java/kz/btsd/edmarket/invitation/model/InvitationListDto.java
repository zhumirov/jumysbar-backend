package kz.btsd.edmarket.invitation.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InvitationListDto {
    private Long eventId;
    private List<String> phones = new ArrayList<>();
    private List<String> emails = new ArrayList<>();
}
