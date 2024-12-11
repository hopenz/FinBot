package ru.naumen.bot.handler.command.impl;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.data.enums.ExpenseCategory;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.interaction.CommandData;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.UserService;

import java.time.LocalDate;
import java.util.List;

/**
 * Тесты для класса {@link ExpensesCommandHandler}, проверяющие обработку команды "/expenses".
 */
public class ExpensesCommandHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для проверки статуса чата.
     */
    private final UserService userServiceMock = Mockito.mock(UserService.class);

    /**
     * Мок-объект для {@link ExpenseService}, используемый для работы с расходами пользователя.
     */
    private final ExpenseService expenseServiceMock = Mockito.mock(ExpenseService.class);

    /**
     * Тестируемый объект {@link ExpensesCommandHandler}, обрабатывающий команду "/expenses".
     */
    private final ExpensesCommandHandler expensesCommandHandler
            = new ExpensesCommandHandler(expenseServiceMock, userServiceMock);

    /**
     * Тест для обработки команды "/expenses".
     * Проверяет, что бот отправляет список расходов пользователя.
     */
    @Test
    void testHandleCommand() throws DaoException {
        long chatId = 12345L;
        List<Expense> expenseList = List.of(
                new Expense("expense1", 100.0,
                        ExpenseCategory.CLOTHING, LocalDate.of(2024, 1, 1)),
                new Expense("expense2", 200.0,
                        ExpenseCategory.SUPERMARKET, LocalDate.of(2024, 2, 2)));
        List<AnswerMessage> expected = List.of(new AnswerMessage(
                """
                        Ваши расходы:
                        100.0 - expense1 (Одежда)
                        200.0 - expense2 (Супермаркет)
                        """, chatId));
        Mockito.when(expenseServiceMock.getExpenses(chatId)).thenReturn(expenseList);

        List<AnswerMessage> response =
                expensesCommandHandler.handleCommand(CommandData.EXPENSES_COMMAND.getReadableName(), chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}