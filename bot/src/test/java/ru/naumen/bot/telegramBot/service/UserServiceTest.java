package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryExpenseDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryIncomeDao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserDao userDaoMock;
    private InMemoryIncomeDao incomeDaoMock;
    private InMemoryExpenseDao expenseDaoMock;
    private UserService userService;
    private BalanceDao balanceDaoMock;

    @BeforeEach
    void setUp() {
        userDaoMock = mock(UserDao.class);
        incomeDaoMock = mock(InMemoryIncomeDao.class);
        expenseDaoMock = mock(InMemoryExpenseDao.class);
        balanceDaoMock = mock(BalanceDao.class);
        userService = new UserService(userDaoMock, incomeDaoMock, expenseDaoMock, balanceDaoMock);
    }

    @Test
    void testIsChatOpened_whenChatIsOpened() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);
        when(userDaoMock.checkChat(12345L)).thenReturn(true);

        boolean isChatOpened = userService.isChatOpened(update);

        assertThat(isChatOpened).isTrue();

        verify(userDaoMock).checkChat(12345L);
    }

    @Test
    void testIsChatOpened_whenChatIsNotOpened() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(54321L);
        when(userDaoMock.checkChat(54321L)).thenReturn(false);

        boolean isChatOpened = userService.isChatOpened(update);

        assertThat(isChatOpened).isFalse();

        verify(userDaoMock).checkChat(54321L);
    }

    @Test
    void testOpenChat_whenChatIsNotOpened() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);
        when(userDaoMock.checkChat(12345L)).thenReturn(false);
        userService.openChat(update);

        verify(userDaoMock).checkChat(12345L);
        verify(userDaoMock).openChat(12345L);
    }

    @Test
    void testOpenChat_whenChatIsOpened() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);

        when(userDaoMock.checkChat(12345L)).thenReturn(true);
        userService.openChat(update);

        verify(userDaoMock).checkChat(12345L);
        verifyNoMoreInteractions(userDaoMock);
    }
}
