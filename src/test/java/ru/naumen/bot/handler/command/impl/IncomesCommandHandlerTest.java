package ru.naumen.bot.handler.command.impl;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.service.IncomeService;
import ru.naumen.bot.service.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Тесты для класса {@link IncomesCommandHandler}, проверяющие обработку команды "/incomes".
 */
public class IncomesCommandHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для проверки статуса чата.
     */
    private UserService userServiceMock;

    /**
     * Мок-объект для {@link IncomeService}, используемый для работы с доходами пользователя.
     */
    private IncomeService incomeServiceMock;

    /**
     * Тестируемый объект {@link IncomesCommandHandler}, обрабатывающий команду "/incomes".
     */
    private IncomesCommandHandler incomesCommandHandler;

    /**
     * Инициализация всех зависимостей и {@link IncomesCommandHandler} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userServiceMock = Mockito.mock(UserService.class);
        incomeServiceMock = Mockito.mock(IncomeService.class);

        incomesCommandHandler = new IncomesCommandHandler(incomeServiceMock, userServiceMock);
    }

    /**
     * Тест для обработки команды INCOME_COMMAND.
     * Проверяет, что бот отправляет список доходов пользователя.
     */
    @Test
    void testHandleCommand() throws DaoException {
        long chatId = 12345L;
        List<Income> incomeList = Arrays.asList(
                new Income("income1", 100.0, LocalDate.of(2024, 1, 1)),
                new Income("income2", 200.0, LocalDate.of(2024, 2, 2)));
        List<AnswerMessage> expected = List.of(new AnswerMessage(
                "Ваши доходы:\n100.0 - income1\n200.0 - income2\n", chatId));
        Mockito.when(incomeServiceMock.getIncomes(chatId)).thenReturn(incomeList);

        List<AnswerMessage> response = incomesCommandHandler.handleCommand(Commands.INCOMES_COMMAND.getCommand(), chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}