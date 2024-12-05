package ru.naumen.bot.handler.callback.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.callback.CallbackHandler;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Обработчик коллбеков для выбора категории расхода при добавлении нового расхода.
 */
@Component
public class ExpenseCategoryForAddingCallbackHandler implements CallbackHandler {

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
    public ExpenseCategoryForAddingCallbackHandler(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    @Override
    public ChatState getChatState() {
        return ChatState.WAITING_EXPENSE_CATEGORY_FOR_ADDING;
    }

    @Override
    public List<AnswerMessage> handleCallback(String callbackData, String callbackId,
                                              long chatId) throws DaoException {
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        expenseService.changeLastExpenseCategory(chatId, callbackData);
        return List.of(new AnswerMessage("Категория расходов сохранена", chatId));
    }
}
