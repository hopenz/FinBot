package ru.naumen.bot.data.dao.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.entity.Expense;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryExpenseDaoTest {

    private InMemoryExpenseDao expenseDao;

    @BeforeEach
    void setUp() {
        expenseDao = new InMemoryExpenseDao();
    }

    @Test
    void testGetExpensesReturnsNullForNewChat() {
        long chatId = 12345L;

        assertThat(expenseDao.getExpenses(chatId)).isNull();
    }

    @Test
    void testCreateUserListInitializesEmptyExpenseList() {
        long chatId = 12345L;

        expenseDao.createUserList(chatId);

        List<Expense> expenses = expenseDao.getExpenses(chatId);
        assertThat(expenses).isNotNull();
        assertThat(expenses).isEmpty();
    }

}