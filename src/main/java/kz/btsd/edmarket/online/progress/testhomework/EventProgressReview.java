package kz.btsd.edmarket.online.progress.testhomework;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Отзыв на студента по курсу
 */
@Data
public class EventProgressReview {
    @NotNull
    private Long userId;
    @NotNull
    private Long eventId;
    private String review;
}
