package ru.naumen.bot.handler.command.impl;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.ExpenseCategory;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.UserService;

import java.time.LocalDate;
import java.util.List;

/**
 * Тесты для класса {@link AllCatOfExpensesCommandHandler}, проверяющие корректность обработки команды
 * "/all_cat_of_expenses".
 */
public class AllCatOfExpensesCommandHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для управления состоянием чата.
     */
    private UserService userServiceMock;

    /**
     * Мок-объект для {@link ExpenseService}, предоставляющий данные о расходах.
     */
    private ExpenseService expenseServiceMock;

    /**
     * Тестируемый объект {@link AllCatOfExpensesCommandHandler}, обрабатывающий команду "/all_cat_of_expenses".
     */
    private AllCatOfExpensesCommandHandler allCatOfExpensesCommandHandler;

    /**
     * Инициализация зависимостей перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userServiceMock = Mockito.mock(UserService.class);
        expenseServiceMock = Mockito.mock(ExpenseService.class);

        allCatOfExpensesCommandHandler = new AllCatOfExpensesCommandHandler(expenseServiceMock, userServiceMock);
    }

    /**
     * Тест обработки команды "/all_cat_of_expenses".
     * Проверяет, что бот корректно рассчитывает и возвращает суммарные расходы по категориям за текущий месяц.
     */
    @Test
    void testHandleCommand() throws DaoException {
        long chatId = 12345L;
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
                                Суммарные расходы по категориям за текущий месяц:
                                %s: 40.0
                                %s: 20.0
                                """.formatted(ExpenseCategory.CLOTHING.getName(), ExpenseCategory.TRANSPORT.getName())
                        , chatId));
        List<AnswerMessage> response =
                allCatOfExpensesCommandHandler.handleCommand(Commands.ALL_CAT_OF_EXPENSES.getCommand(), chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}