package ru.naumen.bot.data.entity;

import java.util.List;
import java.util.Objects;

/**
 * Класс для представления сообщения-ответа, отправляемого пользователю.
 */
public class AnswerMessage {

    /**
     * Текст сообщения, который будет отправлен.
     */
    private final String message;

    /**
     * Идентификатор чата, в который будет отправлено сообщение.
     * Может быть {@code null}, если возвращается всплывающее сообщение.
     */
    private final Long chatId;

    /**
     * Кнопки, которые будут прикреплены к сообщению в виде клавиатуры.
     * Представлены списком списков строк, где каждая строка соответствует тексту кнопки.
     * Может быть {@code null}, если возвращается сообщение без клавиатуры.
     */
    private final List<List<String>> buttons;

    /**
     * Идентификатор callback-запроса, для которого отправляется ответ.
     * Может быть {@code null}, если возвращается текстовое сообщение.
     */
    private final String callbackQueryId;

    /**
     * Конструктор для создания сообщения с текстом и идентификатором чата.
     *
     * @param message текст сообщения
     * @param chatId  идентификатор чата
     */
    public AnswerMessage(String message, Long chatId) {
        this(message, chatId, null, null);
    }

    /**
     * Конструктор для создания сообщения с текстом, идентификатором чата и кнопками.
     *
     * @param message текст сообщения
     * @param chatId  идентификатор чата
     * @param buttons кнопки для сообщения в виде клавиатуры
     */
    public AnswerMessage(String message, Long chatId, List<List<String>> buttons) {
        this(message, chatId, buttons, null);
    }

    /**
     * Конструктор для создания ответа на callback-запрос с текстом и идентификатором callback-запроса.
     *
     * @param message         текст сообщения
     * @param callbackQueryId идентификатор callback-запроса
     */
    public AnswerMessage(String message, String callbackQueryId) {
        this(message, null, null, callbackQueryId);
    }

    /**
     * Приватный конструктор для инициализации всех полей объекта.
     *
     * @param message         текст сообщения
     * @param chatId          идентификатор чата
     * @param buttons         кнопки для сообщения
     * @param callbackQueryId идентификатор callback-запроса
     */
    private AnswerMessage(String message, Long chatId, List<List<String>> buttons, String callbackQueryId) {
        this.message = message;
        this.chatId = chatId;
        this.buttons = buttons;
        this.callbackQueryId = callbackQueryId;
    }

    /**
     * Возвращает текст сообщения.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Возвращает идентификатор чата.
     */
    public Long getChatId() {
        return chatId;
    }

    /**
     * Возвращает кнопки сообщения.
     */
    public List<List<String>> getButtons() {
        return buttons;
    }

    /**
     * Возвращает идентификатор callback-запроса.
     */
    public String getCallbackQueryId() {
        return callbackQueryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerMessage that = (AnswerMessage) o;
        return Objects.equals(message, that.message)
                && Objects.equals(chatId, that.chatId)
                && Objects.equals(buttons, that.buttons)
                && Objects.equals(callbackQueryId, that.callbackQueryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, chatId, buttons, callbackQueryId);
    }
}