package ru.naumen.bot.handler.message;

import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.exception.DaoException;

import java.util.List;

/**
 * Интерфейс для обработки сообщений от пользователей
 */
public interface MessageHandler {

    /**
     * Возвращает состояние чата, при котором необходимо использовать этот обработчик.
     * Возвращаемое значение должно быть уникально во всех классах имплементирующих данный интерфейс.
     */
    ChatState getChatState();

    /**
     * Обрабатывает сообщение, полученное от пользователя.
     *
     * @param message текст сообщения
     * @param chatId  идентификатор чата пользователя
     * @return список сообщений-ответов для пользователя
     * @throws DaoException если произошла ошибка при взаимодействии с базой данных
     */
    List<AnswerMessage> handleMessage(String message, long chatId) throws DaoException;
}
