package ru.naumen.bot.handler.message.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.data.enums.DataType;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.message.MessageHandler;
import ru.naumen.bot.service.DatabaseService;
import ru.naumen.bot.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик сообщений, содержащих ссылку на Google Sheet
 */
@Component
public class MessageWithGoogleSheetLinkHandler implements MessageHandler {

    /**
     * Шаблон регулярного выражения для распознания ссылок на Google Sheet
     */
    private static final String GOOGLE_SHEET_LINK_PATTERN = "^https://docs\\.google\\.com/spreadsheets/d/.{1,200}$";

    /**
     * Сервис для работы с базой данных
     */
    private final DatabaseService databaseService;

    /**
     * Сервис для работы с пользователем
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика сообщений со ссылкой на Google Sheet
     *
     * @param databaseService Сервис для работы с базой данных
     * @param userService     Сервис для работы с пользователем
     */
    public MessageWithGoogleSheetLinkHandler(DatabaseService databaseService, UserService userService) {
        this.databaseService = databaseService;
        this.userService = userService;
    }

    @Override
    public ChatState getChatState() {
        return ChatState.WAITING_FOR_GOOGLE_SHEET_LINK;
    }

    @Override
    public List<AnswerMessage> handleMessage(String message, long chatId) throws DaoException {
        List<AnswerMessage> answerMessages = new ArrayList<>();
        if (message.matches(GOOGLE_SHEET_LINK_PATTERN)) {
            userService.setGoogleSheetId(chatId, message);
            databaseService.changeDB(chatId, DataType.IN_GOOGLE_SHEET);
            answerMessages.add(new AnswerMessage("Бот готов к работе!", chatId));
        } else {
            answerMessages.add(new AnswerMessage("Ссылка на Google Sheet некорректна", chatId));
        }
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        return answerMessages;
    }
}
