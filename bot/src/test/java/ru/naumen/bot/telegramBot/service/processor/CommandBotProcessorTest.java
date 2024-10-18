package ru.naumen.bot.telegramBot.service.processor;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.telegramBot.service.BotService;
import ru.naumen.bot.telegramBot.service.DataService;
import ru.naumen.bot.telegramBot.service.processor.impl.CommandBotProcessor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static ru.naumen.bot.telegramBot.command.Commands.*;

public class CommandBotProcessorTest {

    private BotService botServiceMock;
    private DataService dataServiceMock;
    private CommandBotProcessor commandBotProcessor;

    @BeforeEach
    void setUp() {
        botServiceMock = mock(BotService.class);
        dataServiceMock = mock(DataService.class);
        commandBotProcessor = new CommandBotProcessor(botServiceMock, dataServiceMock);
    }

    @Test
    void testProcessStartCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(START_COMMAND);

        commandBotProcessor.process(update);

        verify(dataServiceMock).openChat(update);
        verify(botServiceMock).sendMessage("Давайте начнём", update);
    }

    @Test
    void testProcessIncomeCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(INCOME_COMMAND);

        List<Income> incomeList = Arrays.asList(
                new Income("income1", 100.0, LocalDate.of(2024, 1, 1)),
                new Income("income2", 200.0, LocalDate.of(2024, 2, 2)));
        when(dataServiceMock.getIncomes(update)).thenReturn(incomeList);

        commandBotProcessor.process(update);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(botServiceMock).sendMessage(messageCaptor.capture(), eq(update));

        String expectedMessage = "Ваши доходы:\n100.0 - income1\n200.0 - income2\n";
        assertThat(messageCaptor.getValue()).isEqualTo(expectedMessage);
    }

    @Test
    void testProcessExpensesCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn(EXPENSES_COMMAND);

        List<Expense> expenseList = Arrays.asList(
                new Expense("expense1", 100.0, LocalDate.of(2024, 1, 1)),
                new Expense("expense2", 200.0, LocalDate.of(2024, 2, 2)));
        when(dataServiceMock.getExpenses(update)).thenReturn(expenseList);

        commandBotProcessor.process(update);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(botServiceMock).sendMessage(messageCaptor.capture(), eq(update));

        String expectedMessage = "Ваши расходы:\n100.0 - expense1\n200.0 - expense2\n";
        assertThat(messageCaptor.getValue()).isEqualTo(expectedMessage);
    }

    @Test
    void testProcessUnknownCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/UNKNOWN_COMMAND");

        commandBotProcessor.process(update);

        verify(botServiceMock).sendMessage("Неизвестная команда", update);
    }
}
