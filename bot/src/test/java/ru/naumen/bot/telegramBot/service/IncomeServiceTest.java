package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.entity.Income;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class IncomeServiceTest {

    private IncomeDao incomeDaoMock;
    private BalanceDao balanceDaoMock;
    private IncomeService incomeService;

    @BeforeEach
    void setUp() {
        incomeDaoMock = mock(IncomeDao.class);
        balanceDaoMock = mock(BalanceDao.class);
        incomeService = new IncomeService(incomeDaoMock, balanceDaoMock);
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

        List<Income> incomes = incomeService.getIncomes(update);

        assertThat(incomes).isEqualTo(expectedIncomes);

        verify(incomeDaoMock).getIncomes(12345L);
    }

    @Test
    void testAddIncome() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(12345L);

        String income = "+ 100000 зарплата";
        incomeService.addIncome(income, update);

        verify(incomeDaoMock).addIncome(12345L, new Income("зарплата", 100000.0, LocalDate.now()));
        verify(balanceDaoMock).setBalance(12345L, 100000.0);
    }
}
