package ru.naumen.bot.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.command.Commands;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
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
     * Тестируемый объект {@link CommandBotProcessor}, который проверяется в данном тестовом классе.
     */
    private CommandBotProcessor commandBotProcessor;

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
        commandBotProcessor = new CommandBotProcessor(botController, userServiceMock, balanceServiceMock,
                incomeServiceMock, expenseServiceMock);
    }

    /**
     * Тест для обработки команды START_COMMAND. Проверяет, что бот
     * отправляет сообщение о начале работы и открывает чат.
     */
    @Test
    void testProcessStartCommand() {
        commandBotProcessor.processCommand(Commands.START_COMMAND.getCommand(), 12345L);

        Mockito.verify(userServiceMock).openChat(12345L);
        Mockito.verify(botController).sendMessage("Давайте начнём", 12345L);
    }

    /**
     * Тест для обработки команды INCOME_COMMAND. Проверяет, что бот отправляет список доходов пользователя.
     */
    @Test
    void testProcessIncomeCommand() {
        List<Income> incomeList = Arrays.asList(
                new Income("income1", 100.0, LocalDate.of(2024, 1, 1)),
                new Income("income2", 200.0, LocalDate.of(2024, 2, 2)));
        Mockito.when(incomeServiceMock.getIncomes(12345L)).thenReturn(incomeList);

        commandBotProcessor.processCommand(Commands.INCOME_COMMAND.getCommand(), 12345L);

        Mockito.verify(botController).sendMessage("Ваши доходы:\n100.0 - income1\n200.0 - income2\n", 12345L);
    }

    /**
     * Тест для обработки команды EXPENSES_COMMAND. Проверяет, что бот отправляет список расходов пользователя.
     */
    @Test
    void testProcessExpensesCommand() {
        List<Expense> expenseList = Arrays.asList(
                new Expense("expense1", 100.0, LocalDate.of(2024, 1, 1)),
                new Expense("expense2", 200.0, LocalDate.of(2024, 2, 2)));
        Mockito.when(expenseServiceMock.getExpenses(12345L)).thenReturn(expenseList);

        commandBotProcessor.processCommand(Commands.EXPENSES_COMMAND.getCommand(), 12345L);

        Mockito.verify(botController).sendMessage("Ваши расходы:\n100.0 - expense1\n200.0 - expense2\n", 12345L);
    }

    /**
     * Тест для обработки команды BALANCE_COMMAND. Проверяет, что бот отправляет текущий баланс пользователя.
     */
    @Test
    void testProcessBalanceCommand() {
        Mockito.when(balanceServiceMock.getBalance(12345L)).thenReturn(100.0);

        commandBotProcessor.processCommand(Commands.BALANCE_COMMAND.getCommand(), 12345L);

        Mockito.verify(botController).sendMessage("Ваш баланс: 100.0", 12345L);
    }

    /**
     * Тест для обработки команды HELP_COMMAND. Проверяет, что бот отправляет справочную информацию по доступным командам.
     */
    @Test
    void testProcessHelpCommand() {
        commandBotProcessor.processCommand(Commands.HELP_COMMAND.getCommand(), 12345L);

        Mockito.verify(botController).sendMessage("""
                Справка по всем командам:
                /start - Начать работу с ботом
                /expenses - Получить информацию о расходах
                /income - Получить информацию о доходах
                /help - Справка по командам
                /balance - Текущий баланс

                Чтобы добавить доход введите:
                + <сумма> <описание>

                Чтобы добавить расход введите:
                - <сумма> <описание>""", 12345L);
    }

    /**
     * Тест для обработки неизвестной команды. Проверяет, что бот отправляет сообщение об ошибке.
     */
    @Test
    void testProcessUnknownCommand() {
        commandBotProcessor.processCommand("/UNKNOWN_COMMAND", 12345L);

        Mockito.verify(botController).sendMessage("Неизвестная команда", 12345L);
    }
}
