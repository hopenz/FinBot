package ru.naumen.bot.telegramBot.processor;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.telegramBot.processor.impl.MessageBotProcessor;
import ru.naumen.bot.telegramBot.service.BotService;
import ru.naumen.bot.telegramBot.service.ExpenseService;
import ru.naumen.bot.telegramBot.service.IncomeService;

import static org.mockito.Mockito.*;
import static ru.naumen.bot.telegramBot.command.Commands.HELP_COMMAND;

public class MessageBotProcessorTest {

    private BotService botServiceMock;
    private IncomeService incomeServiceMock;
    private ExpenseService expenseServiceMock;
    private MessageBotProcessor messageBotProcessor;

    @BeforeEach
    void setUp() {
        botServiceMock = mock(BotService.class);
        incomeServiceMock = mock(IncomeService.class);
        expenseServiceMock = mock(ExpenseService.class);
        messageBotProcessor = new MessageBotProcessor(botServiceMock, incomeServiceMock, expenseServiceMock);
    }

    @Test
    void testProcessAddIncome() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("+ 100 чаевые");

        messageBotProcessor.processMessage(update);

        verify(incomeServiceMock).addIncome("+ 100 чаевые", update);
        verify(botServiceMock).sendMessage("Доход успешно добавлен!", update);
    }

    @Test
    void testProcessAddExpense() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("- 100 автобус");

        messageBotProcessor.processMessage(update);

        verify(expenseServiceMock).addExpense("- 100 автобус", update);
        verify(botServiceMock).sendMessage("Расход успешно добавлен!", update);
    }

    @Test
    void testProcessWhenTextNotMatchAnyPattern() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("- 123мяу");

        messageBotProcessor.processMessage(update);

        verifyNoMoreInteractions(expenseServiceMock);
        verifyNoMoreInteractions(incomeServiceMock);

        verify(botServiceMock).sendMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                + HELP_COMMAND, update);
    }
}
