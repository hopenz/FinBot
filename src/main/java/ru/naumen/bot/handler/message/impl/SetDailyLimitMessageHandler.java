package ru.naumen.bot.handler.message.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.message.MessageHandler;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Обработчик сообщений для установки ежедневного лимита расходов пользователя
 */
@Component
public class SetDailyLimitMessageHandler implements MessageHandler {

    /**
     * Сервис для взаимодействия с данными пользователя.
     */
    private final UserService userService;

    /**
     * Сервис для управления расходами.
     */
    private final ExpenseService expenseService;

    /**
     * Конструктор класса {@link SetDailyLimitMessageHandler}.
     *
     * @param userService    Сервис для управления состоянием пользователя.
     * @param expenseService Сервис для управления расходами.
     */
    public SetDailyLimitMessageHandler(UserService userService, ExpenseService expenseService) {
        this.userService = userService;
        this.expenseService = expenseService;
    }

    @Override
    public ChatState getChatState() {
        return ChatState.WAITING_DAILY_LIMIT;
    }

    @Override
    public List<AnswerMessage> handleMessage(String message, long chatId) throws DaoException {
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        try {
            expenseService.setExpensesLimit(chatId, message);
            return List.of(new AnswerMessage("Лимит расходов на день установлен!", chatId));
        } catch (NumberFormatException e) {
            return List.of(new AnswerMessage("Некорректное значение лимита расходов!", chatId));
        } catch (IllegalArgumentException e) {
            return List.of(new AnswerMessage("Лимит не может быть отрицательным или нулём!", chatId));
        }
    }
}
