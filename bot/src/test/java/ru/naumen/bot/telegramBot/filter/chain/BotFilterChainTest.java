package ru.naumen.bot.telegramBot.filter.chain;

import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.telegramBot.filter.ABotFilter;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.mockito.Mockito.*;

public class BotFilterChainTest {
    private BotFilterChain botFilterChain;
    private ABotFilter filter1;
    private ABotFilter filter2;
    private Update update;

    @BeforeEach
    void setUp() throws Exception {
        filter1 = mock(ABotFilter.class);
        filter2 = mock(ABotFilter.class);
        update = mock(Update.class);

        botFilterChain = new BotFilterChain(Arrays.asList(filter1, filter2));

        Method method = BotFilterChain.class.getDeclaredMethod("configureFilterChain");
        method.setAccessible(true);
        method.invoke(botFilterChain);

        doAnswer(invocation -> {
            filter2.doFilter(invocation.getArgument(0));
            return null;
        }).when(filter1).doFilter(any(Update.class));
    }

    @Test
    void testProcessWithFilters() {
        verify(filter1).setNextFilter(filter2);

        botFilterChain.process(update);

        verify(filter1).doFilter(update);
        verify(filter2).doFilter(update);
        verifyNoMoreInteractions(filter1, filter2);
    }
}
