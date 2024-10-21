package ru.naumen.bot.telegramBot.filter.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.telegramBot.filter.ABotFilter;
import ru.naumen.bot.telegramBot.filter.filterImpl.MessageFilter;
import ru.naumen.bot.telegramBot.processor.impl.MessageBotProcessor;

import static org.mockito.Mockito.*;

public class MessageFilterTest {

    private MessageFilter messageFilter;
    private MessageBotProcessor messageBotProcessor;
    private Update update;
    private Message message;

    @BeforeEach
    void setUp() {
        messageBotProcessor = mock(MessageBotProcessor.class);
        update = mock(Update.class);
        message = mock(Message.class);

        when(update.message()).thenReturn(message);

        messageFilter = new MessageFilter(messageBotProcessor);
    }

    @Test
    void testDoFilter_WithMessage_ShouldProcessMessageAndCallNextFilter() {
        when(message.text()).thenReturn("gdfhdfgh");
        ABotFilter nextFilter = mock(ABotFilter.class);
        messageFilter.setNextFilter(nextFilter);

        messageFilter.doFilter(update);

        verify(messageBotProcessor).process(update);
        verifyNoMoreInteractions(messageBotProcessor);

        verify(nextFilter).doFilter(update);
    }

    @Test
    void testDoFilter_WithMessage_ShouldProcessMessageWithoutCallNextFilter() {
        when(message.text()).thenReturn("gdfhdfgh");

        messageFilter.doFilter(update);

        verify(messageBotProcessor).process(update);
        verifyNoMoreInteractions(messageBotProcessor);

    }

}
