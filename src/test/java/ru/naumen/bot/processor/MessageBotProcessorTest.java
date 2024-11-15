package ru.naumen.bot.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.service.DatabaseService;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.IncomeService;
import ru.naumen.bot.service.UserService;

/**
 * Тесты для класса {@link MessageBotProcessor}, проверяющие корректность обработки текстовых сообщений.
 */
public class MessageBotProcessorTest {

    /**
     * Мок-объект для {@link BotController}, используемый для отправки сообщений пользователям.
     */
    private BotController botController;

    /**
     * Мок-объект для {@link IncomeService}, используемый для работы с доходами пользователя.
     */
    private IncomeService incomeServiceMock;

    /**
     * Мок-объект для {@link ExpenseService}, используемый для работы с расходами пользователя.
     */
    private ExpenseService expenseServiceMock;

    /**
     * Мок-объект для {@link UserService}, используемый для работы с данными о пользователях.
     */
    private UserService userServiceMock;

    /**
     * Мок-объект для {@link DatabaseService}, используемый для работы с базами данных.
     */
    private DatabaseService databaseServiceMock;

    /**
     * Тестируемый объект {@link MessageBotProcessor}, который проверяется в данном тестовом классе.
     */
    private MessageBotProcessor messageBotProcessor;

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345L;

    /**
     * Инициализация всех зависимостей и {@link MessageBotProcessor} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        botController = Mockito.mock(BotController.class);
        incomeServiceMock = Mockito.mock(IncomeService.class);
        expenseServiceMock = Mockito.mock(ExpenseService.class);
        userServiceMock = Mockito.mock(UserService.class);
        databaseServiceMock = Mockito.mock(DatabaseService.class);
        messageBotProcessor = new MessageBotProcessor(botController, incomeServiceMock, expenseServiceMock,
                userServiceMock, databaseServiceMock);
    }

    /**
     * Тест для проверки необходимости выполнения команды START_COMMAND перед обработкой других сообщений.
     * Проверяет, что если чат не был начат, бот предлагает пользователю сначала выполнить команду START_COMMAND.
     */
    @Test
    void testProcessMessage_requiresStart() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(false);

        messageBotProcessor.processMessage("+ 100 Salary", chatId);

        Mockito.verify(botController).sendMessage("Чтобы начать работу, нажмите " +
                Commands.START_COMMAND.getCommand(), chatId);
    }

    /**
     * Тест для обработки сообщения с валидной ссылкой на Google Sheet, когда бот ожидает ссылку.
     * Проверяет, что ссылка сохраняется, отправляется сообщение о подготовке к работе, и база данных меняется.
     */
    @Test
    void testProcessMessage_withValidGoogleSheetLink() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);

        String googleSheetLink = "https://docs.google.com/spreadsheets/d/validSheetID";
        messageBotProcessor.processMessage(googleSheetLink, chatId);

        Mockito.verify(userServiceMock).setGoogleSheetLink(chatId, googleSheetLink);
        Mockito.verify(botController).sendMessage("Создаю страницы документа, подготавливаю к работе …\n", chatId);
        Mockito.verify(userServiceMock).setDataType(chatId, DataType.IN_GOOGLE_SHEET);
        Mockito.verify(databaseServiceMock).changeDB(chatId, DataType.IN_GOOGLE_SHEET);
        Mockito.verify(botController).sendMessage("Бот готов к работе!", chatId);
    }

    /**
     * Тест для обработки сообщения с невалидной ссылкой на Google Sheet, когда бот ожидает ссылку.
     * Проверяет, что бот отправляет сообщение об ошибке.
     */
    @Test
    void testProcessMessage_withInvalidGoogleSheetLink() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);

        String invalidLink = "https://invalid.link";
        messageBotProcessor.processMessage(invalidLink, chatId);

        Mockito.verify(botController).sendMessage("Данная ссылка не верна", chatId);
    }

    /**
     * Тест для обработки добавления дохода. Проверяет, что метод
     * добавления дохода вызывается корректно и отправляется сообщение об успехе.
     */
    @Test
    void testProcessAddIncome() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.NOTHING_WAITING);

        messageBotProcessor.processMessage("+ 100 чаевые", chatId);

        Mockito.verify(incomeServiceMock).addIncome("+ 100 чаевые", chatId);
        Mockito.verify(botController).sendMessage("Доход успешно добавлен!", chatId);
    }

    /**
     * Тест для обработки добавления расхода. Проверяет, что метод
     * добавления расхода вызывается корректно и отправляется сообщение об успехе.
     */
    @Test
    void testProcessAddExpense() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.NOTHING_WAITING);

        messageBotProcessor.processMessage("- 100 автобус", chatId);

        Mockito.verify(expenseServiceMock).addExpense("- 100 автобус", chatId);
        Mockito.verify(botController).sendMessage("Расход успешно добавлен!", chatId);
    }

    /**
     * Тест для обработки случая, когда текст сообщения не соответствует ни одному из шаблонов.
     * Проверяет, что не вызываются методы добавления доходов или расходов,
     * и отправляется сообщение о некорректности сообщения.
     */
    @Test
    void testProcessWhenTextNotMatchAnyPattern() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.NOTHING_WAITING);

        messageBotProcessor.processMessage("- 123мяу", 12345L);

        Mockito.verifyNoMoreInteractions(expenseServiceMock);
        Mockito.verifyNoMoreInteractions(incomeServiceMock);

        Mockito.verify(botController).sendMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                + Commands.HELP_COMMAND.getCommand(), 12345L);
    }
}
