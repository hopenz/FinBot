package ru.naumen.bot.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.exception.GoogleSheetsException;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.processor.exception.handler.GoogleSheetsExceptionHandler;
import ru.naumen.bot.service.BalanceService;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.IncomeService;
import ru.naumen.bot.service.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Тесты для класса {@link CommandBotProcessor}, проверяющие корректность обработки команд.
 */
public class CommandBotProcessorTest {

    /**
     * Мок-объект для {@link BotController}, используемый для отправки сообщений пользователям.
     */
    private BotController botController;

    /**
     * Мок-объект для {@link UserService}, используемый для проверки статуса чата.
     */
    private UserService userServiceMock;

    /**
     * Мок-объект для {@link IncomeService}, используемый для работы с доходами пользователя.
     */
    private IncomeService incomeServiceMock;

    /**
     * Мок-объект для {@link ExpenseService}, используемый для работы с расходами пользователя.
     */
    private ExpenseService expenseServiceMock;

    /**
     * Мок-объект для {@link BalanceService}, используемый для управления балансом пользователя.
     */
    private BalanceService balanceServiceMock;

    /**
     * Мок-объект для {@link GoogleSheetsExceptionHandler}, используемый для обработки исключений Google Sheets.
     */
    private GoogleSheetsExceptionHandler exceptionHandler;

    /**
     * Тестируемый объект {@link CommandBotProcessor}, который проверяется в данном тестовом классе.
     */
    private CommandBotProcessor commandBotProcessor;

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345L;

    /**
     * Инициализация всех зависимостей и commandBotProcessor перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        botController = Mockito.mock(BotController.class);
        userServiceMock = Mockito.mock(UserService.class);
        incomeServiceMock = Mockito.mock(IncomeService.class);
        expenseServiceMock = Mockito.mock(ExpenseService.class);
        balanceServiceMock = Mockito.mock(BalanceService.class);
        exceptionHandler = Mockito.mock(GoogleSheetsExceptionHandler.class);

        commandBotProcessor = new CommandBotProcessor(botController, userServiceMock, balanceServiceMock,
                incomeServiceMock, expenseServiceMock, exceptionHandler);
    }

    /**
     * Тест для обработки команды START_COMMAND. Проверяет, что бот
     * отправляет сообщение о начале работы и открывает чат.
     */
    @Test
    void testProcessStartCommand() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(false);

        commandBotProcessor.processCommand(Commands.START_COMMAND.getCommand(), chatId);

        Mockito.verify(userServiceMock).openChat(chatId);
        Mockito.verify(botController).sendMessage(
                "Здравствуйте! Как вы хотите хранить данные?", chatId,
                Arrays.stream(TypeDBKeyboard.values())
                        .map(TypeDBKeyboard::getData)
                        .toList());
    }

    /**
     * Тест для обработки команды INCOME_COMMAND. Проверяет, что бот отправляет список доходов пользователя.
     */
    @Test
    void testProcessIncomeCommand() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        List<Income> incomeList = Arrays.asList(
                new Income("income1", 100.0, LocalDate.of(2024, 1, 1)),
                new Income("income2", 200.0, LocalDate.of(2024, 2, 2)));
        Mockito.when(incomeServiceMock.getIncomes(chatId)).thenReturn(incomeList);

        commandBotProcessor.processCommand(Commands.INCOMES_COMMAND.getCommand(), chatId);

        Mockito.verify(botController).sendMessage("Ваши доходы:\n100.0 - income1\n200.0 - income2\n", chatId);
    }

    /**
     * Тест для обработки команды INCOMES_COMMAND при возникновении исключения GoogleSheetsException.
     * Проверяет, что бот отправляет сообщение об ошибке и вызывает обработчик исключений.
     */
    @Test
    void testProcessIncomeCommandWithGoogleSheetsException() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        GoogleSheetsException exception = new GoogleSheetsException(new Exception());
        Mockito.when(incomeServiceMock.getIncomes(chatId)).thenThrow(exception);

        commandBotProcessor.processCommand(Commands.INCOMES_COMMAND.getCommand(), chatId);

        Mockito.verify(botController).sendMessage("Во время отправки доходов произошла ошибка", chatId);
        Mockito.verify(exceptionHandler).handleGoogleSheetsException(exception, chatId);
    }

    /**
     * Тест для обработки команды EXPENSES_COMMAND. Проверяет, что бот отправляет список расходов пользователя.
     */
    @Test
    void testProcessExpensesCommand() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        List<Expense> expenseList = Arrays.asList(
                new Expense("expense1", 100.0, LocalDate.of(2024, 1, 1)),
                new Expense("expense2", 200.0, LocalDate.of(2024, 2, 2)));
        Mockito.when(expenseServiceMock.getExpenses(chatId)).thenReturn(expenseList);

        commandBotProcessor.processCommand(Commands.EXPENSES_COMMAND.getCommand(), chatId);

        Mockito.verify(botController).sendMessage("Ваши расходы:\n100.0 - expense1\n200.0 - expense2\n",
                chatId);
    }

    /**
     * Тест для обработки команды EXPENSES_COMMAND при возникновении исключения GoogleSheetsException.
     * Проверяет, что бот отправляет сообщение об ошибке и вызывает обработчик исключений.
     */
    @Test
    void testProcessExpensesCommandWithGoogleSheetsException() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        GoogleSheetsException exception = new GoogleSheetsException(new Exception());
        Mockito.when(expenseServiceMock.getExpenses(chatId)).thenThrow(exception);

        commandBotProcessor.processCommand(Commands.EXPENSES_COMMAND.getCommand(), chatId);

        Mockito.verify(botController).sendMessage("Во время отправки расходов произошла ошибка", chatId);
        Mockito.verify(exceptionHandler).handleGoogleSheetsException(exception, chatId);
    }

    /**
     * Тест для обработки команды BALANCE_COMMAND. Проверяет, что бот отправляет текущий баланс пользователя.
     */
    @Test
    void testProcessBalanceCommand() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        Mockito.when(balanceServiceMock.getBalance(chatId)).thenReturn(100.0);

        commandBotProcessor.processCommand(Commands.BALANCE_COMMAND.getCommand(), chatId);

        Mockito.verify(botController).sendMessage("Ваш баланс: 100.0", chatId);
    }

    /**
     * Тест для обработки команды BALANCE_COMMAND при возникновении исключения GoogleSheetsException.
     * Проверяет, что бот отправляет сообщение об ошибке и вызывает обработчик исключений.
     */
    @Test
    void testProcessBalanceCommandWithGoogleSheetsException() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        GoogleSheetsException exception = new GoogleSheetsException(new Exception());
        Mockito.when(balanceServiceMock.getBalance(chatId)).thenThrow(exception);

        commandBotProcessor.processCommand(Commands.BALANCE_COMMAND.getCommand(), chatId);

        Mockito.verify(botController).sendMessage("Во время отправки баланса произошла ошибка", chatId);
        Mockito.verify(exceptionHandler).handleGoogleSheetsException(exception, chatId);
    }

    /**
     * Тест для проверки обработки команды CHANGE_DB_COMMAND.
     * Проверяет, что состояние пользователя изменяется на WAITING_FOR_TYPE_DB_FOR_CHANGE_DB,
     * и отправляется сообщение с клавиатурой выбора базы данных.
     */
    @Test
    void testProcessCommand_changeDbCommand() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);

        commandBotProcessor.processCommand(Commands.CHANGE_DB_COMMAND.getCommand(), chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.WAITING_FOR_TYPE_DB_FOR_CHANGE_DB);
        Mockito.verify(botController).sendMessage("Выберите базу данных", chatId,
                Arrays.stream(TypeDBKeyboard.values())
                        .map(TypeDBKeyboard::getData)
                        .toList());
    }

    /**
     * Тест для обработки неизвестной команды. Проверяет, что бот отправляет сообщение об ошибке.
     */
    @Test
    void testProcessUnknownCommand() {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        commandBotProcessor.processCommand("/UNKNOWN_COMMAND", chatId);

        Mockito.verify(botController).sendMessage("Неизвестная команда", chatId);
    }
}