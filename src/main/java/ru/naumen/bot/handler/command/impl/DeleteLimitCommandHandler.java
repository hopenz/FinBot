package ru.naumen.bot.handler.command.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.CommandData;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Обработчик команды для удаления лимита расходов пользователя
 */
@Component
public class DeleteLimitCommandHandler implements CommandHandler {

    /**
     * Сервис для взаимодействия с данными пользователя.
     */
    private final UserService userService;

    /**
     * Сервис для управления расходами.
     */
    private final ExpenseService expenseService;

    /**
     * Конструктор класса {@link DeleteLimitCommandHandler}.
     *
     * @param userService    Сервис для управления состоянием пользователя.
     * @param expenseService Сервис для управления расходами.
     */
    public DeleteLimitCommandHandler(UserService userService, ExpenseService expenseService) {
        this.userService = userService;
        this.expenseService = expenseService;
    }

    @Override
    public String getCommand() {
        return CommandData.DELETE_EXPENSE_LIMIT_COMMAND.getReadableName();
    }

    @Override
    public List<AnswerMessage> handleCommand(String message, long chatId) throws DaoException {
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        expenseService.removeExpensesLimit(chatId);
        return List.of(new AnswerMessage("Лимит расходов на день удален!", chatId));
    }
}
