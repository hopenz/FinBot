package ru.naumen.bot.handler.command.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.interaction.CommandData;
import ru.naumen.bot.service.BalanceService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Тесты для класса {@link BalanceCommandHandler}, проверяющие обработку команды "/balance".
 */
public class BalanceCommandHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для управления состоянием пользователя.
     */
    private final UserService userServiceMock = Mockito.mock(UserService.class);

    /**
     * Мок-объект для {@link BalanceService}, предоставляющий информацию о балансе пользователя.
     */
    private final BalanceService balanceServiceMock = Mockito.mock(BalanceService.class);

    /**
     * Тестируемый объект {@link BalanceCommandHandler}, обрабатывающий команду "/balance".
     */
    private final BalanceCommandHandler balanceCommandHandler
            = new BalanceCommandHandler(balanceServiceMock, userServiceMock);

    /**
     * Тест для обработки команды "/balance".
     * Проверяет, что бот корректно возвращает баланс пользователя.
     */
    @Test
    void testHandleCommand() throws DaoException {
        long chatId = 12345L;
        Mockito.when(balanceServiceMock.getBalance(chatId)).thenReturn(100.0);
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("Ваш баланс: 100.0", chatId));

        List<AnswerMessage> response =
                balanceCommandHandler.handleCommand(CommandData.BALANCE_COMMAND.getReadableName(), chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}