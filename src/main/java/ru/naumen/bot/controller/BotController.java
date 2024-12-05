package ru.naumen.bot.controller;

import ru.naumen.bot.data.entity.AnswerMessage;

import java.util.List;

/**
 * Интерфейс, представляющий методы для работы с чат-ботами.
 */
public interface BotController {

    /**
     * Отправка сообщения в указанный чат
     *
     * @param message сообщение, которое будет отправлено
     * @param chatId  идентификатор чата, в который будет отправлено сообщение
     */
    void sendMessage(String message, long chatId);

    /**
     * Отправка сообщения в указанный чат с использованием инлайн-клавиатуры
     *
     * @param message сообщение, которое будет отправлено
     * @param chatId  идентификатор чата, в котором будет отправлено сообщение
     * @param buttons список кнопок для инлайн-клавиатуры, которая будет отправлена с сообщением
     */
    void sendMessage(String message, long chatId, List<List<String>> buttons);

    /**
     * Отправка ответа на нажатие кнопки инлайн-клавиатуры во всплывающем сообщении
     *
     * @param message         текст ответа
     * @param callbackQueryId идентификатор callback-запроса
     */
    void sendPopUpMessage(String message, String callbackQueryId);

    /**
     * Отправка нескольких сообщений в указанный чат.
     * Сообщения могут быть разного типа.
     *
     * @param answerMessages сообщения разного типа.
     */
    void sendMessages(List<AnswerMessage> answerMessages);
}
