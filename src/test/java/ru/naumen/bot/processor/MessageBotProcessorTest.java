package ru.naumen.bot.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.processor.exception.handler.GoogleSheetsExceptionHandler;
import ru.naumen.bot.service.DatabaseService;
import ru.naumen.bot.service.FinanceDataService;
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
     * Мок-объект для {@link FinanceDataService}, используемый для взаимодействия с данными о финансах.
     */
    private FinanceDataService financeDataService;

    /**
     * Мок-объект для {@link UserService}, используемый для работы с данными о пользователях.
     */
    private UserService userServiceMock;

    /**
     * Мок-объект для {@link DatabaseService}, используемый для работы с базами данных.
     */
    private DatabaseService databaseServiceMock;

    /**
     * Мок-объект для {@link GoogleSheetsExceptionHandler}, используемый для обработки исключений в Google Sheets.
     */
    private GoogleSheetsExceptionHandler exceptionHandler;

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
        financeDataService = Mockito.mock(FinanceDataService.class);
        userServiceMock = Mockito.mock(UserService.class);
        databaseServiceMock = Mockito.mock(DatabaseService.class);
        exceptionHandler = Mockito.mock(GoogleSheetsExceptionHandler.class);

        messageBotProcessor = new MessageBotProcessor(botController, financeDataService, userServiceMock,
                databaseServiceMock, exceptionHandler);
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
    void testProcessMessage_withValidGoogleSheetLink() throws DaoException {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);

        String googleSheetLink = "https://docs.google.com/spreadsheets/d/validSheetID";
        messageBotProcessor.processMessage(googleSheetLink, chatId);

        Mockito.verify(userServiceMock).setGoogleSheetId(chatId, googleSheetLink);
        Mockito.verify(botController).sendMessage("Создаю страницы документа, подготавливаю к работе …\n", chatId);
        Mockito.verify(databaseServiceMock).changeDB(chatId, DataType.IN_GOOGLE_SHEET);
        Mockito.verify(botController).sendMessage("Бот готов к работе!", chatId);
    }

    /**
     * Тест для обработки сообщения с валидной ссылкой на Google Sheet, когда бот ожидает ссылку
     * и возникает GoogleSheetsException.
     * Проверяет, что бот отправляет сообщение об ошибке и вызывает обработчик исключений.
     */
    @Test
    void testProcessMessage_withValidGoogleSheetLinkAndGoogleSheetsException() throws DaoException {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);
        GoogleSheetsException exception = new GoogleSheetsException(new Exception());
        Mockito.doThrow(exception).when(databaseServiceMock).changeDB(chatId, DataType.IN_GOOGLE_SHEET);

        String googleSheetLink = "https://docs.google.com/spreadsheets/d/validSheetID";
        messageBotProcessor.processMessage(googleSheetLink, chatId);

        Mockito.verify(userServiceMock).setGoogleSheetId(chatId, googleSheetLink);
        Mockito.verify(botController).sendMessage("Создаю страницы документа, подготавливаю к работе …\n", chatId);
        Mockito.verify(botController).sendMessage("Во время инициализации таблицы произошла ошибка", chatId);
        Mockito.verify(exceptionHandler).handleGoogleSheetsException(exception, chatId);
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
    void testProcessAddIncome() throws DaoException {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.NOTHING_WAITING);

        messageBotProcessor.processMessage("+ 100 чаевые", chatId);

        Mockito.verify(financeDataService).addIncome("+ 100 чаевые", chatId);
        Mockito.verify(botController).sendMessage("Доход успешно добавлен!", chatId);
    }

    /**
     * Тест для обработки добавления дохода, при возникновении GoogleSheetsException.
     * Проверяет, что бот отправляет сообщение об ошибке и вызывает обработчик исключений.
     */
    @Test
    void testProcessAddIncomeWithGoogleSheetsException() throws DaoException {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.NOTHING_WAITING);
        GoogleSheetsException exception = new GoogleSheetsException(new Exception());
        Mockito.doThrow(exception).when(financeDataService).addIncome("+ 100 чаевые", chatId);

        messageBotProcessor.processMessage("+ 100 чаевые", chatId);

        Mockito.verify(botController).sendMessage("Во время добавления дохода произошла ошибка", chatId);
        Mockito.verify(exceptionHandler).handleGoogleSheetsException(exception, chatId);
    }

    /**
     * Тест для обработки добавления расхода. Проверяет, что метод
     * добавления расхода вызывается корректно и отправляется сообщение об успехе.
     */
    @Test
    void testProcessAddExpense() throws DaoException {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.NOTHING_WAITING);

        messageBotProcessor.processMessage("- 100 автобус", chatId);

        Mockito.verify(financeDataService).addExpense("- 100 автобус", chatId);
        Mockito.verify(botController).sendMessage("Расход успешно добавлен!", chatId);
    }

    /**
     * Тест для обработки добавления расхода, при возникновении GoogleSheetsException.
     * Проверяет, что бот отправляет сообщение об ошибке и вызывает обработчик исключений.
     */
    @Test
    void testProcessAddExpenseWithGoogleSheetsException() throws DaoException {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.NOTHING_WAITING);
        GoogleSheetsException exception = new GoogleSheetsException(new Exception());
        Mockito.doThrow(exception).when(financeDataService).addExpense("- 100 автобус", chatId);

        messageBotProcessor.processMessage("- 100 автобус", chatId);

        Mockito.verify(botController).sendMessage("Во время добавления расхода произошла ошибка", chatId);
        Mockito.verify(exceptionHandler).handleGoogleSheetsException(exception, chatId);
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

        Mockito.verifyNoMoreInteractions(financeDataService);

        Mockito.verify(botController).sendMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                + Commands.HELP_COMMAND.getCommand(), 12345L);
    }
}
