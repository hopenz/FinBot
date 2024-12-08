package ru.naumen.bot.handler.command;

import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.exception.DaoException;

import java.util.List;

/**
 * Интерфейс для обработчиков команд, отправляемых боту.
 */
public interface CommandHandler {

    /**
     * Возвращает текст команды, которую обрабатывает данный обработчик.
     * Возвращаемое значение должно быть уникально во всех классах имплементирующих данный интерфейс.
     */
    String getCommand();

    /**
     * Обрабатывает команду, отправленную пользователем.
     *
     * @param message текст команды
     * @param chatId  идентификатор чата, откуда пришла команда
     * @return список сообщений-ответов для отправки пользователю
     * @throws DaoException если возникает ошибка работа с данными
     */
    List<AnswerMessage> handleCommand(String message, long chatId) throws DaoException;
}
