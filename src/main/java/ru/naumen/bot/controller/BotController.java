package ru.naumen.bot.controller;

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
}
