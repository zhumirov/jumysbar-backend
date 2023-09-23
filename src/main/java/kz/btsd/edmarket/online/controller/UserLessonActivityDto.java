package kz.btsd.edmarket.online.controller;

import lombok.Data;

/**
 * Активность пользователя на уроке
 */
@Data
public class UserLessonActivityDto {
    private long likes;
    private long dislikes;
    //количество ответов на дз
    private long comments;
    //моя домашняя работа
    private long myHomework;
}
