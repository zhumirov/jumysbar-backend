package kz.btsd.edmarket.user.model.link;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteInviteLinkRequest {
    @NotNull
    private String uuid;
}
