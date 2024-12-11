package ru.naumen.bot.handler.message.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.exception.ExceedingTheLimitException;
import ru.naumen.bot.handler.message.MessageHandler;
import ru.naumen.bot.interaction.CommandData;
import ru.naumen.bot.interaction.keyboards.CategoriesKeyboard;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.IncomeService;
import ru.naumen.bot.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик простых сообщений, связанных с добавлением доходов и расходов.
 */
@Component
public class SimpleMessageHandler implements MessageHandler {

    /**
     * Шаблон регулярного выражения для распознания сообщений о доходах
     */
    private static final String INCOMES_PATTERN = "^\\+\\s\\d{1,8}(\\.\\d{1,2})?\\s.{0,100}$";

    /**
     * Шаблон регулярного выражения для распознания сообщений о расходах
     */
    private static final String EXPENSES_PATTERN = "^-\\s\\d{1,8}(\\.\\d{1,2})?\\s.{0,100}$";

    /**
     * Количество кнопок на одной строке в клавиатуре для выбора категории расхода.
     */
    private static final int COUNT_BUTTONS_AT_ROW = 3;

    /**
     * Сервис для работы с расходами.
     */
    private final ExpenseService expenseService;

    /**
     * Сервис для работы с доходами.
     */
    private final IncomeService incomeService;

    /**
     * Сервис для работы с пользователем.
     */
    private final UserService userService;

    /**
     * Класс для формирования клавиатуры с категориями расходов
     */
    private final CategoriesKeyboard categoriesKeyboard;

    /**
     * Конструктор для инициализации обработчика сообщений.
     *
     * @param expenseService     Сервис для работы с расходами
     * @param incomeService      Сервис для работы с доходами
     * @param userService        Сервис для работы с данными пользователя
     * @param categoriesKeyboard Класс для формирования клавиатуры с категориями расходов
     */
    public SimpleMessageHandler(ExpenseService expenseService, IncomeService incomeService,
                                UserService userService, CategoriesKeyboard categoriesKeyboard) {
        this.expenseService = expenseService;
        this.incomeService = incomeService;
        this.userService = userService;
        this.categoriesKeyboard = categoriesKeyboard;
    }

    @Override
    public ChatState getChatState() {
        return ChatState.NOTHING_WAITING;
    }

    @Override
    public List<AnswerMessage> handleMessage(String message, long chatId) throws DaoException {
        List<AnswerMessage> answerMessages = new ArrayList<>();
        if (message.matches(INCOMES_PATTERN)) {
            incomeService.addIncome(message, chatId);
            return List.of(new AnswerMessage("Доход успешно добавлен!", chatId));
        }
        if (message.matches(EXPENSES_PATTERN)) {
            try {
                expenseService.addExpense(message, chatId);
            } catch (ExceedingTheLimitException e) {
                answerMessages.add(new AnswerMessage("Лимит расходов на день превышен!", chatId));
                answerMessages.add(new AnswerMessage("Сумма расходов за день составляет: "
                        + e.getDailyExpensesSum(), chatId));
            }
            userService.setUserState(chatId, ChatState.WAITING_EXPENSE_CATEGORY_FOR_ADDING);
            List<List<String>> keyboardButtons = categoriesKeyboard.getCategoriesInGroups(COUNT_BUTTONS_AT_ROW);
            answerMessages.add(new AnswerMessage("Выберите категорию расхода", chatId, keyboardButtons));
            return answerMessages;
        }
        return List.of(new AnswerMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                + CommandData.HELP_COMMAND.getReadableName(), chatId));
    }
}
