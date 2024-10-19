package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DataServiceTest {

    private UserDao userDaoMock;
    private IncomeDao incomeDaoMock;
    private ExpenseDao expenseDaoMock;
    private DataService dataService;

    @BeforeEach
    void setUp() {
        userDaoMock = mock(UserDao.class);
        incomeDaoMock = mock(IncomeDao.class);
        expenseDaoMock = mock(ExpenseDao.class);
        //dataService = new DataService(userDaoMock, incomeDaoMock, expenseDaoMock);
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

        boolean isChatOpened = dataService.isChatOpened(update);

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

        boolean isChatOpened = dataService.isChatOpened(update);

        assertThat(isChatOpened).isFalse();

        verify(userDaoMock).checkChat(54321L);
    }

    @Test
    void testOpenChat() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);

        dataService.openChat(update);

        verify(userDaoMock).openChat(12345L);
    }

    @Test
    void testGetIncomes() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);

        List<Income> expectedIncomes = Arrays.asList(
                new Income("income1", 100.0, LocalDate.of(2024, 1, 1)),
                new Income("income2", 200.0, LocalDate.of(2024, 2, 2)));
        when(incomeDaoMock.getIncomes(12345L)).thenReturn(expectedIncomes);

        List<Income> incomes = dataService.getIncomes(update);

        assertThat(incomes).isEqualTo(expectedIncomes);

        verify(incomeDaoMock).getIncomes(12345L);
    }

    @Test
    void testGetExpenses() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);

        List<Expense> expectedExpenses = Arrays.asList(
                new Expense("expense1", 100.0, LocalDate.of(2024, 1, 1)),
                new Expense("expense2", 200.0, LocalDate.of(2024, 2, 2)));
        when(expenseDaoMock.getExpenses(12345L)).thenReturn(expectedExpenses);

        List<Expense> expenses = dataService.getExpenses(update);

        assertThat(expenses).isEqualTo(expectedExpenses);

        verify(expenseDaoMock).getExpenses(12345L);
    }
}
