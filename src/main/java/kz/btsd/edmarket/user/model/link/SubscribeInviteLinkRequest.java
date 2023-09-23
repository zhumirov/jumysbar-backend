package kz.btsd.edmarket.user.model.link;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubscribeInviteLinkRequest {
    @NotNull
    private Long userId;
    @NotNull
    private String uuid;
}
