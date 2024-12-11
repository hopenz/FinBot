package ru.naumen.bot.data.entity;

/**
 * Класс-обертка для обновлений, получаемых ботом.
 *
 * @param chatId       Идентификатор чата, из которого пришло обновление
 * @param message      Текст сообщения, отправленного пользователем
 * @param callbackData Callback-данные, отправленные пользователем
 * @param callbackId   Идентификатор callback-запроса
 */
public record BotUpdate(Long chatId, String message, String callbackData, String callbackId) {

    /**
     * Проверка, является ли обновление текстовым сообщением
     *
     * @return true, если это текстовое сообщение, иначе false
     */
    public boolean isTextMessage() {
        return message != null;
    }

    /**
     * Проверка, является ли обновление callback-запросом (нажатием на кнопку)
     *
     * @return true, если это callback-запрос, иначе false
     */
    public boolean isCallbackQuery() {
        return callbackData != null && callbackId != null;
    }
}