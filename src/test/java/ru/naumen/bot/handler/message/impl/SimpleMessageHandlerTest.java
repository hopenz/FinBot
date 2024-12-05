package ru.naumen.bot.handler.message.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.interaction.keyboards.CategoriesKeyboard;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.IncomeService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Тесты для класса {@link SimpleMessageHandler}, проверяющие обработку различных входящих сообщений.
 */
public class SimpleMessageHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для работы с данными пользователей.
     */
    private UserService userServiceMock;

    /**
     * Мок-объект для {@link ExpenseService}, используемый для работы с расходами пользователей.
     */
    private ExpenseService expenseServiceMock;

    /**
     * Мок-объект для {@link IncomeService}, используемый для работы с доходами пользователей.
     */
    private IncomeService incomeServiceMock;

    /**
     * Мок-объект для {@link CategoriesKeyboard}, используемый для предоставления клавиатуры с категориями расходов.
     */
    private CategoriesKeyboard categoriesKeyboardMock;

    /**
     * Тестируемый объект {@link SimpleMessageHandler}, который обрабатывает входящие сообщения.
     */
    private SimpleMessageHandler simpleMessageHandler;

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345L;

    /**
     * Инициализация всех зависимостей и {@link SimpleMessageHandler} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userServiceMock = Mockito.mock(UserService.class);
        expenseServiceMock = Mockito.mock(ExpenseService.class);
        incomeServiceMock = Mockito.mock(IncomeService.class);
        categoriesKeyboardMock = Mockito.mock(CategoriesKeyboard.class);

        simpleMessageHandler = new SimpleMessageHandler(expenseServiceMock, incomeServiceMock,
                userServiceMock, categoriesKeyboardMock);
    }

    /**
     * Тест для проверки обработки сообщения с доходом.
     * Проверяет, что доход корректно добавляется и возвращается ожидаемый ответ.
     */
    @Test
    void testHandleMessagedWithIncomeMessage() throws DaoException {
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("Доход успешно добавлен!", chatId));

        List<AnswerMessage> response =
                simpleMessageHandler.handleMessage("+ 888 Доход", chatId);

        Mockito.verify(incomeServiceMock).addIncome("+ 888 Доход", chatId);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тест для проверки обработки сообщения с расходом.
     * Проверяет, что расход корректно добавляется, и возвращается клавиатура для выбора категории.
     */
    @Test
    void testHandleMessageWithExpenseMessage() throws DaoException {
        List<List<String>> keyboardButtons = List.of(List.of("Категория 1", "Категория 2", "Категория 3"));
        Mockito.when(categoriesKeyboardMock.getCategoriesInGroups(3)).thenReturn(keyboardButtons);
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("Выберите категорию расхода", chatId, keyboardButtons));

        List<AnswerMessage> response =
                simpleMessageHandler.handleMessage("- 666 Расход", chatId);

        Mockito.verify(expenseServiceMock).addExpense("- 666 Расход", chatId);
        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.WAITING_EXPENSE_CATEGORY_FOR_ADDING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тест для проверки обработки некорректного сообщения.
     * Проверяет, что при некорректном сообщении возвращается сообщение об ошибке и сервисы не вызываются.
     */
    @Test
    void testHandleMessageWithInvalidMessage() throws DaoException {
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                        + Commands.HELP_COMMAND.getCommand(), chatId));

        List<AnswerMessage> response =
                simpleMessageHandler.handleMessage("+888 Доход", chatId);

        Mockito.verifyNoInteractions(incomeServiceMock);
        Mockito.verifyNoInteractions(expenseServiceMock);
        Mockito.verifyNoInteractions(userServiceMock);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}