package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.entity.Expense;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    private ExpenseDao expenseDaoMock;
    private BalanceDao balanceDaoMock;
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseDaoMock = mock(ExpenseDao.class);
        balanceDaoMock = mock(BalanceDao.class);
        expenseService = new ExpenseService(expenseDaoMock, balanceDaoMock);
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

        List<Expense> expenses = expenseService.getExpenses(update);

        assertThat(expenses).isEqualTo(expectedExpenses);

        verify(expenseDaoMock).getExpenses(12345L);
    }

    @Test
    void testAddExpense() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);

        String expense = "- 100000 покупка сумки";
        expenseService.addExpense(expense, update);

        verify(expenseDaoMock).addExpense(12345L, new Expense("покупка сумки",
                100000.0, LocalDate.now()));
        verify(balanceDaoMock).setBalance(12345L, -100000.0);
    }
}
