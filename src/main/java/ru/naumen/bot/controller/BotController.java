package ru.naumen.bot.controller;

import ru.naumen.bot.data.entity.AnswerMessage;

import java.util.List;

/**
 * Интерфейс, представляющий методы для работы с чат-ботами.
 */
public interface BotController {

    /**
     * Отправка нескольких сообщений в указанный чат.
     * Сообщения могут быть разного типа.
     *
     * @param answerMessages сообщения разного типа.
     */
    void sendMessages(List<AnswerMessage> answerMessages);
}
