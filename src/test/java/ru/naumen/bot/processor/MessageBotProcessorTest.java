package ru.naumen.bot.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.command.Commands;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.IncomeService;

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
     * Тестируемый объект {@link MessageBotProcessor}, который проверяется в данном тестовом классе.
     */
    private MessageBotProcessor messageBotProcessor;

    /**
     * Инициализация всех зависимостей и {@link MessageBotProcessor} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        botController = Mockito.mock(BotController.class);
        incomeServiceMock = Mockito.mock(IncomeService.class);
        expenseServiceMock = Mockito.mock(ExpenseService.class);
        messageBotProcessor = new MessageBotProcessor(botController, incomeServiceMock, expenseServiceMock);
    }

    /**
     * Тест для обработки добавления дохода. Проверяет, что метод
     * добавления дохода вызывается корректно и отправляется сообщение об успехе.
     */
    @Test
    void testProcessAddIncome() {
        messageBotProcessor.processMessage("+ 100 чаевые", 12345L);

        Mockito.verify(incomeServiceMock).addIncome("+ 100 чаевые", 12345L);
        Mockito.verify(botController).sendMessage("Доход успешно добавлен!", 12345L);
    }

    /**
     * Тест для обработки добавления расхода. Проверяет, что метод
     * добавления расхода вызывается корректно и отправляется сообщение об успехе.
     */
    @Test
    void testProcessAddExpense() {
        messageBotProcessor.processMessage("- 100 автобус", 12345L);

        Mockito.verify(expenseServiceMock).addExpense("- 100 автобус", 12345L);
        Mockito.verify(botController).sendMessage("Расход успешно добавлен!", 12345L);
    }

    /**
     * Тест для обработки случая, когда текст сообщения не соответствует ни одному из шаблонов.
     * Проверяет, что не вызываются методы добавления доходов или расходов,
     * и отправляется сообщение о некорректности сообщения.
     */
    @Test
    void testProcessWhenTextNotMatchAnyPattern() {
        messageBotProcessor.processMessage("- 123мяу", 12345L);

        Mockito.verifyNoMoreInteractions(expenseServiceMock);
        Mockito.verifyNoMoreInteractions(incomeServiceMock);

        Mockito.verify(botController).sendMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                + Commands.HELP_COMMAND.getCommand(), 12345L);
    }
}
