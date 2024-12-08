package ru.naumen.bot.handler.command.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Обработчик команды для установки лимита расходов пользователя
 */
@Component
public class LimitCommandHandler implements CommandHandler {

    /**
     * Сервис для взаимодействия с данными пользователя.
     */
    private final UserService userService;

    /**
     * Конструктор класса {@link LimitCommandHandler}.
     *
     * @param userService Сервис для взаимодействия с данными пользователя.
     */
    public LimitCommandHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getCommand() {
        return Commands.SET_EXPENSES_LIMIT_COMMAND.getCommand();
    }

    @Override
    public List<AnswerMessage> handleCommand(String message, long chatId) throws DaoException {
        userService.setUserState(chatId, ChatState.WAITING_DAILY_LIMIT);
        return List.of(new AnswerMessage("Введите лимит расходов на день", chatId));
    }
}
