package ru.naumen.bot.handler.callback.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.callback.CallbackHandler;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.UserService;

import java.time.LocalDate;
import java.util.List;

/**
 * Обработчик коллбеков для вывода расходов по выбранной категории
 */
@Component
public class ExpenseCategoryForOutputCallbackHandler implements CallbackHandler {

    /**
     * Сервис для работы с расходами
     */
    private final ExpenseService expenseService;

    /**
     * Сервис для работы с пользователем
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param expenseService сервис для работы с расходами.
     * @param userService    сервис для работы с пользователями.
     */
    public ExpenseCategoryForOutputCallbackHandler(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    @Override
    public ChatState getChatState() {
        return ChatState.WAITING_EXPENSE_CATEGORY_FOR_OUTPUT;
    }

    @Override
    public List<AnswerMessage> handleCallback(String callbackData, String callbackId, long chatId) throws DaoException {
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        List<Expense> expenses = expenseService.getExpenses(chatId);
        StringBuilder result = new StringBuilder();
        result.append("Расходы по категории '").append(callbackData).append("' за текущий месяц:\n");
        LocalDate today = LocalDate.now();
        LocalDate startPeriod = LocalDate.of(today.getYear(), today.getMonth(), 1);
        for (int i = expenses.size() - 1; i >= 0; i--) {
            Expense expense = expenses.get(i);
            if (expense.getDate().isBefore(startPeriod)) {
                break;
            }
            if (expense.getCategory().getName().equals(callbackData)) {
                result.append(expense.getAmount()).append(" - ").append(expense.getDescription()).append("\n");
            }
        }
        return List.of(new AnswerMessage(result.toString(), chatId));
    }
}
