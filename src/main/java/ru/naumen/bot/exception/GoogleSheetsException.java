package ru.naumen.bot.exception;

/**
 * Исключение, возникающее при ошибках, связанных с работой с Google Sheets
 */
public class GoogleSheetsException extends RuntimeException {

    /**
     * Идентификатор чата
     */
    private final Long chatId;

    /**
     * Сообщение для бота, которое будет отправлено пользователю
     */
    private final String messageForBot;

    /**
     * Конструктор исключения
     *
     * @param message       Сообщение об ошибке
     * @param chatId        Идентификатор чата
     * @param messageForBot Сообщение для бота
     */
    public GoogleSheetsException(String message, Long chatId, String messageForBot) {
        super(message);
        this.chatId = chatId;
        this.messageForBot = messageForBot;
    }

    /**
     * Возвращает идентификатор чата, при работе с которым возникло исключение
     *
     * @return Идентификатор чата
     */
    public Long getChatId() {
        return chatId;
    }

    /**
     * Возвращает сообщение для бота, которое будет отправлено пользователю
     *
     * @return Сообщение для бота
     */
    public String getMessageForBot() {
        return messageForBot;
    }
}
