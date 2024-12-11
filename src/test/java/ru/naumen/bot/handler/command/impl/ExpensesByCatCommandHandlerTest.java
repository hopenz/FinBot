package ru.naumen.bot.handler.command.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.interaction.CommandData;
import ru.naumen.bot.interaction.keyboards.CategoriesKeyboard;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Тесты для класса {@link ExpensesByCatCommandHandler}, проверяющие обработку команды "/expenses_by_cat".
 */
public class ExpensesByCatCommandHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для управления состоянием пользователя.
     */
    private final UserService userServiceMock = Mockito.mock(UserService.class);

    /**
     * Мок-объект для {@link CategoriesKeyboard}, используемый для формирования кнопок выбора категорий.
     */
    private final CategoriesKeyboard categoriesKeyboardMock = Mockito.mock(CategoriesKeyboard.class);

    /**
     * Тестируемый объект {@link ExpensesByCatCommandHandler}, обрабатывающий команду "/expenses_by_cat".
     */
    private final ExpensesByCatCommandHandler expensesByCatCommandHandler
            = new ExpensesByCatCommandHandler(userServiceMock, categoriesKeyboardMock);

    /**
     * Тест для обработки команды "/expenses_by_cat".
     * Проверяет, что бот отправляет сообщение с предложением выбрать категорию.
     */
    @Test
    void testHandleCommand() throws DaoException {
        long chatId = 12345L;
        List<List<String>> keyboardButtons = List.of(List.of("Категория 1", "Категория 2", "Категория 3"));
        Mockito.when(categoriesKeyboardMock.getCategoriesInGroups(3)).thenReturn(keyboardButtons);
        List<AnswerMessage> expected = List.of(new AnswerMessage(
                "Выберите категорию для просмотра расходов", chatId, keyboardButtons));

        List<AnswerMessage> response =
                expensesByCatCommandHandler.handleCommand(CommandData.EXPENSES_BY_CAT.getReadableName(), chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.WAITING_EXPENSE_CATEGORY_FOR_OUTPUT);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }


}