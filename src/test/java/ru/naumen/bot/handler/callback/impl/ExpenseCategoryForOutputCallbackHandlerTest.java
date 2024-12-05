package ru.naumen.bot.handler.callback.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.ExpenseCategory;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.UserService;

import java.time.LocalDate;
import java.util.List;

/**
 * Тесты для обработчика коллбэков {@link ExpenseCategoryForOutputCallbackHandler}, который обрабатывает
 * вывод расходов по выбранной категории за текущий месяц.
 */
public class ExpenseCategoryForOutputCallbackHandlerTest {

    /**
     * Мок-объект для {@link ExpenseService}, используемый для получения расходов пользователя.
     */
    private ExpenseService expenseServiceMock;

    /**
     * Мок-объект для {@link UserService}, используемый для управления состоянием пользователя.
     */
    private UserService userServiceMock;

    /**
     * Тестируемый обработчик коллбэков {@link ExpenseCategoryForOutputCallbackHandler}.
     */
    private ExpenseCategoryForOutputCallbackHandler callbackHandler;

    /**
     * Инициализация зависимостей перед каждым тестом.
     */
    @BeforeEach
    public void setUp() {
        expenseServiceMock = Mockito.mock(ExpenseService.class);
        userServiceMock = Mockito.mock(UserService.class);
        callbackHandler = new ExpenseCategoryForOutputCallbackHandler(expenseServiceMock, userServiceMock);
    }

    /**
     * Тест обработки коллбэка для вывода расходов по выбранной категории.
     * Проверяет, что бот правильно выводит расходы по категории за текущий месяц.
     */
    @Test
    void testHandleCallback() throws DaoException {
        long chatId = 12345L;
        String callbackId = "id";
        List<Expense> expenses = List.of(
                new Expense("Расход 1", 10.0,
                        ExpenseCategory.CLOTHING, LocalDate.now().minusMonths(1)),
                new Expense("Расход 2", 10.0,
                        ExpenseCategory.CLOTHING, LocalDate.now()),
                new Expense("Расход 3", 20.0,
                        ExpenseCategory.TRANSPORT, LocalDate.now()),
                new Expense("Расход 4", 30.0,
                        ExpenseCategory.CLOTHING, LocalDate.now())
        );
        Mockito.when(expenseServiceMock.getExpenses(chatId)).thenReturn(expenses);

        List<AnswerMessage> expected =
                List.of(new AnswerMessage(
                        """
                                Расходы по категории '%s' за текущий месяц:
                                30.0 - Расход 4
                                10.0 - Расход 2
                                """.formatted(ExpenseCategory.CLOTHING.getName())
                        , chatId));
        List<AnswerMessage> response =
                callbackHandler.handleCallback(ExpenseCategory.CLOTHING.getName(), callbackId, chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}