package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.dao.BalanceDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BalanceServiceTest {

    private BalanceDao balanceDaoMock;
    private BalanceService balanceService;

    @BeforeEach
    void setUp() {
        balanceDaoMock = mock(BalanceDao.class);
        balanceService = new BalanceService(balanceDaoMock);
    }

    @Test
    void testGetBalance() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);
        when(balanceDaoMock.getBalance(12345L)).thenReturn(100000000.0);

        double result = balanceService.getBalance(update);

        assertThat(result).isEqualTo(100000000.0);
        verify(balanceDaoMock).getBalance(12345L);
    }
}
