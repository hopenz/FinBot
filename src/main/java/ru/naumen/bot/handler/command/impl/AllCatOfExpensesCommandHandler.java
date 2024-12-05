package ru.naumen.bot.handler.command.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.ExpenseCategory;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.UserService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Обработчик команды для получения суммарных расходов по категориям за текущий месяц
 */
@Component
public class AllCatOfExpensesCommandHandler implements CommandHandler {

    /**
     * Сервис для взаимодействия с данными расходов.
     */
    private final ExpenseService expenseService;

    /**
     * Сервис для взаимодействия с данными пользователя.
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param expenseService сервис для работы с расходами
     * @param userService    сервис для работы с пользователями
     */
    public AllCatOfExpensesCommandHandler(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    @Override
    public String getCommand() {
        return Commands.ALL_CAT_OF_EXPENSES.getCommand();
    }

    @Override
    public List<AnswerMessage> handleCommand(String message, long chatId) throws DaoException {
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        List<Expense> expenses = expenseService.getExpenses(chatId);
        Map<ExpenseCategory, Double> sumExpenses = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate startPeriod = LocalDate.of(today.getYear(), today.getMonth(), 1);
        for (int i = expenses.size() - 1; i >= 0; i--) {
            Expense expense = expenses.get(i);
            if (expense.getDate().isBefore(startPeriod)) {
                break;
            }
            ExpenseCategory category = expense.getCategory();
            sumExpenses.put(category, sumExpenses.getOrDefault(category, 0.0) + expense.getAmount());
        }
        StringBuilder result = new StringBuilder();
        result.append("Суммарные расходы по категориям за текущий месяц:\n");
        List<Map.Entry<ExpenseCategory, Double>> sortedEntries = sumExpenses.entrySet().stream()
                .sorted(Comparator.comparingDouble(entry -> -entry.getValue()))
                .toList();
        for (Map.Entry<ExpenseCategory, Double> entry : sortedEntries) {
            result.append(entry.getKey().getName()).append(": ").append(entry.getValue()).append("\n");
        }
        return List.of(new AnswerMessage(result.toString(), chatId));
    }
}
