package ru.naumen.bot.telegramBot.filter.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.telegramBot.filter.ABotFilter;
import ru.naumen.bot.telegramBot.filter.filterImpl.CommandFilter;
import ru.naumen.bot.telegramBot.processor.impl.CommandBotProcessor;

import static org.mockito.Mockito.*;

public class CommandFilterTest {


    private CommandFilter commandFilter;
    private CommandBotProcessor commandBotProcessor;
    private Update update;
    private Message message;

    @BeforeEach
    void setUp() {
        commandBotProcessor = mock(CommandBotProcessor.class);
        update = mock(Update.class);
        message = mock(Message.class);

        when(update.message()).thenReturn(message);

        commandFilter = new CommandFilter(commandBotProcessor);
    }

    @Test
    void testDoFilter_WithCommand_ShouldProcessCommand() {
        when(message.text()).thenReturn("/start");

        commandFilter.doFilter(update);

        verify(commandBotProcessor).process(update);

        verifyNoMoreInteractions(commandBotProcessor);
    }

    @Test
    void testDoFilter_WithoutCommand_ShouldCallNextFilter() {
        when(message.text()).thenReturn("hello");

        ABotFilter nextFilter = mock(ABotFilter.class);
        commandFilter.setNextFilter(nextFilter);

        commandFilter.doFilter(update);

        verifyNoMoreInteractions(commandBotProcessor);
        verify(nextFilter).doFilter(update);
    }

    @Test
    void testDoFilter_WithoutCommand_NoNextFilter() {
        when(message.text()).thenReturn("hello");

        commandFilter.doFilter(update);

        verifyNoMoreInteractions(commandBotProcessor);
    }
}
