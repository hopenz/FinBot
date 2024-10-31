package ru.naumen.bot.telegramBot.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.telegramBot.controller.TelegramBotController;
import ru.naumen.bot.telegramBot.service.ExpenseService;
import ru.naumen.bot.telegramBot.service.IncomeService;

import static org.mockito.Mockito.*;
import static ru.naumen.bot.telegramBot.command.Commands.HELP_COMMAND;

/**
 * Тесты для класса {@link MessageBotProcessor}, проверяющие корректность обработки текстовых сообщений.
 */
public class MessageBotProcessorTest {

    /**
     * Мок-объект для {@link TelegramBotController}, используемый для отправки сообщений пользователям.
     */
    private TelegramBotController botController;

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
        botController = mock(TelegramBotController.class);
        incomeServiceMock = mock(IncomeService.class);
        expenseServiceMock = mock(ExpenseService.class);
        messageBotProcessor = new MessageBotProcessor(botController, incomeServiceMock, expenseServiceMock);
    }

    /**
     * Тест для обработки добавления дохода. Проверяет, что метод
     * добавления дохода вызывается корректно и отправляется сообщение об успехе.
     */
    @Test
    void testProcessAddIncome() {
        messageBotProcessor.processMessage("+ 100 чаевые", 12345L);

        verify(incomeServiceMock).addIncome("+ 100 чаевые", 12345L);
        verify(botController).sendMessage("Доход успешно добавлен!", 12345L);
    }

    /**
     * Тест для обработки добавления расхода. Проверяет, что метод
     * добавления расхода вызывается корректно и отправляется сообщение об успехе.
     */
    @Test
    void testProcessAddExpense() {
        messageBotProcessor.processMessage("- 100 автобус", 12345L);

        verify(expenseServiceMock).addExpense("- 100 автобус", 12345L);
        verify(botController).sendMessage("Расход успешно добавлен!", 12345L);
    }

    /**
     * Тест для обработки случая, когда текст сообщения не соответствует ни одному из шаблонов.
     * Проверяет, что не вызываются методы добавления доходов или расходов,
     * и отправляется сообщение о некорректности сообщения.
     */
    @Test
    void testProcessWhenTextNotMatchAnyPattern() {
        messageBotProcessor.processMessage("- 123мяу", 12345L);

        verifyNoMoreInteractions(expenseServiceMock);
        verifyNoMoreInteractions(incomeServiceMock);

        verify(botController).sendMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                + HELP_COMMAND, 12345L);
    }
}
