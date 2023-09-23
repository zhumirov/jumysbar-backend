package kz.btsd.edmarket.user.model.link;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateInviteLinkRequest {
    @NotNull
    private Long eventId;
    @NotNull
    private InviteType type;
}
