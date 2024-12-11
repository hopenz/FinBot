package ru.naumen.bot.handler.callback;

import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.exception.DaoException;

import java.util.List;

/**
 * Интерфейс для обработки коллбеков, полученных от пользователя.
 */
public interface CallbackHandler {

    /**
     * Возвращает состояние чата, при котором необходимо использовать этот обработчик.
     * Возвращаемое значение должно быть уникально во всех классах имплементирующих данный интерфейс.
     */
    ChatState getChatState();

    /**
     * Обработка коллбека, полученного от пользователя.
     *
     * @param callbackData данные, отправленные пользователем с коллбеком.
     * @param callbackId   уникальный идентификатор коллбека.
     * @param chatId       идентификатор чата пользователя, с которым связано действие.
     * @return список сообщений, которые бот должен отправить в ответ на коллбек.
     * @throws DaoException если возникла ошибка при взаимодействии с базой данных.
     */
    List<AnswerMessage> handleCallback(String callbackData, String callbackId, long chatId) throws DaoException;
}
