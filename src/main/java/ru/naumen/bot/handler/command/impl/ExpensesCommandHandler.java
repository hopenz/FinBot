package ru.naumen.bot.handler.command.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Обработчик команды для вывода всех расходов пользователя
 */
@Component
public class ExpensesCommandHandler implements CommandHandler {

    /**
     * Сервис для работы с расходами
     */
    private final ExpenseService expenseService;

    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param expenseService сервис для работы с расходами
     * @param userService    сервис для работы с пользователями
     */
    public ExpensesCommandHandler(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    @Override
    public String getCommand() {
        return Commands.EXPENSES_COMMAND.getCommand();
    }

    @Override
    public List<AnswerMessage> handleCommand(String message, long chatId) throws DaoException {
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        List<Expense> expenseList = expenseService.getExpenses(chatId);
        StringBuilder expenses = new StringBuilder();
        expenses.append("Ваши расходы:\n");
        for (Expense expense : expenseList) {
            expenses.append(expense.getAmount()).append(" - ").append(expense.getDescription()).append(" (").
                    append(expense.getCategory().getName()).append(")\n");
        }
        return List.of(new AnswerMessage(expenses.toString(), chatId));
    }
}
