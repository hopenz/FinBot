package ru.naumen.bot.telegramBot.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.telegramBot.controller.TelegramBotController;
import ru.naumen.bot.telegramBot.service.BalanceService;
import ru.naumen.bot.telegramBot.service.ExpenseService;
import ru.naumen.bot.telegramBot.service.IncomeService;
import ru.naumen.bot.telegramBot.service.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static ru.naumen.bot.telegramBot.command.Commands.*;

/**
 * Тесты для класса {@link CommandBotProcessor}, проверяющие корректность обработки команд.
 */
public class CommandBotProcessorTest {

    /**
     * Мок-объект для {@link TelegramBotController}, используемый для отправки сообщений пользователям.
     */
    private TelegramBotController botController;

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
     * Тестируемый объект {@link CommandBotProcessor}, который проверяется в данном тестовом классе.
     */
    private CommandBotProcessor commandBotProcessor;

    /**
     * Инициализация всех зависимостей и commandBotProcessor перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        botController = mock(TelegramBotController.class);
        userServiceMock = mock(UserService.class);
        incomeServiceMock = mock(IncomeService.class);
        expenseServiceMock = mock(ExpenseService.class);
        balanceServiceMock = mock(BalanceService.class);
        commandBotProcessor = new CommandBotProcessor(botController, userServiceMock, balanceServiceMock,
                incomeServiceMock, expenseServiceMock);
    }

    /**
     * Тест для обработки команды START_COMMAND. Проверяет, что бот
     * отправляет сообщение о начале работы и открывает чат.
     */
    @Test
    void testProcessStartCommand() {
        commandBotProcessor.processCommand(START_COMMAND, 12345L);

        verify(userServiceMock).openChat(12345L);
        verify(botController).sendMessage("Давайте начнём", 12345L);
    }

    /**
     * Тест для обработки команды INCOME_COMMAND. Проверяет, что бот отправляет список доходов пользователя.
     */
    @Test
    void testProcessIncomeCommand() {
        List<Income> incomeList = Arrays.asList(
                new Income("income1", 100.0, LocalDate.of(2024, 1, 1)),
                new Income("income2", 200.0, LocalDate.of(2024, 2, 2)));
        when(incomeServiceMock.getIncomes(12345L)).thenReturn(incomeList);

        commandBotProcessor.processCommand(INCOME_COMMAND, 12345L);

        verify(botController).sendMessage("Ваши доходы:\n100.0 - income1\n200.0 - income2\n", 12345L);
    }

    /**
     * Тест для обработки команды EXPENSES_COMMAND. Проверяет, что бот отправляет список расходов пользователя.
     */
    @Test
    void testProcessExpensesCommand() {
        List<Expense> expenseList = Arrays.asList(
                new Expense("expense1", 100.0, LocalDate.of(2024, 1, 1)),
                new Expense("expense2", 200.0, LocalDate.of(2024, 2, 2)));
        when(expenseServiceMock.getExpenses(12345L)).thenReturn(expenseList);

        commandBotProcessor.processCommand(EXPENSES_COMMAND, 12345L);

        verify(botController).sendMessage("Ваши расходы:\n100.0 - expense1\n200.0 - expense2\n", 12345L);
    }

    /**
     * Тест для обработки команды BALANCE_COMMAND. Проверяет, что бот отправляет текущий баланс пользователя.
     */
    @Test
    void testProcessBalanceCommand() {
        when(balanceServiceMock.getBalance(12345L)).thenReturn(100.0);

        commandBotProcessor.processCommand(BALANCE_COMMAND, 12345L);

        verify(botController).sendMessage("Ваш баланс: 100.0", 12345L);
    }

    /**
     * Тест для обработки команды HELP_COMMAND. Проверяет, что бот отправляет справочную информацию по доступным командам.
     */
    @Test
    void testProcessHelpCommand() {
        commandBotProcessor.processCommand(HELP_COMMAND, 12345L);

        verify(botController).sendMessage("Справка по всем командам: \n" +
                "/start - Начать работу с ботом\n" +
                "/expenses - Получить расходы\n" +
                "/income - Показать доходы\n" +
                "/help - Справка по командам\n" +
                "/balance - Текущий баланс\n" +
                "\n" +
                "Чтобы добавить доход введите:\n" +
                "+ <сумма> <описание>\n" +
                "\n" +
                "Чтобы добавить расход введите:\n" +
                "- <сумма> <описание>", 12345L);
    }

    /**
     * Тест для обработки неизвестной команды. Проверяет, что бот отправляет сообщение об ошибке.
     */
    @Test
    void testProcessUnknownCommand() {
        commandBotProcessor.processCommand("/UNKNOWN_COMMAND", 12345L);

        verify(botController).sendMessage("Неизвестная команда", 12345L);
    }
}
