package kz.btsd.edmarket.online.progress.testhomework;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Последний просмотренный/пройденный урок
 */
@Data
public class EventProgressLastStep {
    @NotNull
    private Long userId;
    @NotNull
    private Long eventId;
    @NotNull
    private Long lastStepId;
}
