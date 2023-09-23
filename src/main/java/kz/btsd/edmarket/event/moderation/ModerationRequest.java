package kz.btsd.edmarket.event.moderation;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ModerationRequest {

    @NotNull
    private Long eventId;

    @NotNull
    private ModerationStatus moderationStatus;

    private String comment;

    public ModerationRequest() {
    }
}
