package ru.naumen.bot.handler.message.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Тесты для класса {@link SetDailyLimitMessageHandler}, проверяющие работу обработчика сообщений
 * для установки ежедневного лимита расходов пользователя.
 */
public class SetDailyLimitMessageHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для работы с данными пользователей.
     */
    private final UserService userServiceMock = Mockito.mock(UserService.class);

    /**
     * Мок-объект для {@link ExpenseService}, используемый для работы с расходами пользователей.
     */
    private final ExpenseService expenseServiceMock = Mockito.mock(ExpenseService.class);

    /**
     * Тестируемый экземпляр {@link SetDailyLimitMessageHandler}.
     */
    private final SetDailyLimitMessageHandler setDailyLimitMessageHandler
            = new SetDailyLimitMessageHandler(userServiceMock, expenseServiceMock);

    /**
     * Идентификатор чата, используемый в тестах.
     */
    private final long chatId = 12345L;

    /**
     * Тестирует успешное выполнение установки лимита расходов.
     */
    @Test
    void testHandleMessageSuccess() throws DaoException {
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("Лимит расходов на день установлен!", chatId));

        List<AnswerMessage> response =
                setDailyLimitMessageHandler.handleMessage("500.0", chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Mockito.verify(expenseServiceMock).setExpensesLimit(chatId, "500.0");
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тестирует обработку исключения {@link NumberFormatException}, при некорректном вводе.
     */
    @Test
    void testHandleMessageWithNumberFormatException() throws DaoException {
        Mockito.doThrow(new NumberFormatException()).
                when(expenseServiceMock).setExpensesLimit(chatId, "5dd00");
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("Некорректное значение лимита расходов!", chatId));
        List<AnswerMessage> response =
                setDailyLimitMessageHandler.handleMessage("5dd00", chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тестирует обработку исключения {@link IllegalArgumentException}, при отрицательном или нулевом лимите.
     */
    @Test
    void testHandleMessageWithIllegalArgumentException() throws DaoException {
        Mockito.doThrow(new IllegalArgumentException()).
                when(expenseServiceMock).setExpensesLimit(chatId, "-500.0");
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("Лимит не может быть отрицательным или нулём!", chatId));
        List<AnswerMessage> response =
                setDailyLimitMessageHandler.handleMessage("-500.0", chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}