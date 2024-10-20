package ru.naumen.bot.telegramBot.filter.impl;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.telegramBot.filter.ABotFilter;
import ru.naumen.bot.telegramBot.filter.filterImpl.StartFilter;
import ru.naumen.bot.telegramBot.service.BotService;
import ru.naumen.bot.telegramBot.service.UserService;

import static org.mockito.Mockito.*;
import static ru.naumen.bot.telegramBot.command.Commands.START_COMMAND;

public class StartFilterTest {
    private StartFilter startFilter;
    private BotService botService;
    private UserService userService;
    private Update update;
    private Message message;

    @BeforeEach
    void setUp() {
        botService = mock(BotService.class);
        userService = mock(UserService.class);
        update = mock(Update.class);
        message = mock(Message.class);

        when(update.message()).thenReturn(message);

        startFilter = new StartFilter(botService, userService);
    }

    @Test
    void testDoFilter_NotStartCommandAndChatNotOpened_ShouldSendMessage() {
        when(message.text()).thenReturn("hello");

        when(userService.isChatOpened(update)).thenReturn(false);

        startFilter.doFilter(update);

        verify(botService).sendMessage("Чтобы начать работу, нажмите " + START_COMMAND, update);

        verifyNoMoreInteractions(botService);
    }

    @Test
    void testDoFilter_StartCommand_ShouldCallNextFilter() {
        when(message.text()).thenReturn(START_COMMAND);

        ABotFilter nextFilter = mock(ABotFilter.class);
        startFilter.setNextFilter(nextFilter);

        // Вызываем фильтр
        startFilter.doFilter(update);

        verify(botService, never()).sendMessage(anyString(), eq(update));

        verify(nextFilter).doFilter(update);
    }

    @Test
    void testDoFilter_ChatOpened_ShouldCallNextFilter() {
        when(message.text()).thenReturn("hello");

        when(userService.isChatOpened(update)).thenReturn(true);

        ABotFilter nextFilter = mock(ABotFilter.class);
        startFilter.setNextFilter(nextFilter);

        startFilter.doFilter(update);

        verify(botService, never()).sendMessage(anyString(), eq(update));

        verify(nextFilter).doFilter(update);
    }

    @Test
    void testDoFilter_ChatNotOpenedAndNoNextFilter_ShouldSendMessage() {
        when(message.text()).thenReturn("hello");

        when(userService.isChatOpened(update)).thenReturn(false);

        startFilter.doFilter(update);

        verify(botService).sendMessage("Чтобы начать работу, нажмите " + START_COMMAND, update);

        verifyNoMoreInteractions(botService);
    }
}
