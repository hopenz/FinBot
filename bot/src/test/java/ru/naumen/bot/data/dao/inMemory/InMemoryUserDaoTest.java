package ru.naumen.bot.data.dao.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class InMemoryUserDaoTest {

    private InMemoryIncomeDao incomeDao;
    private InMemoryExpenseDao expenseDao;
    private InMemoryUserDao userDao;

    @BeforeEach
    void setUp() {
        incomeDao = new InMemoryIncomeDao();
        expenseDao = new InMemoryExpenseDao();
        userDao = new InMemoryUserDao(incomeDao, expenseDao);
    }

    @Test
    void testCheckChatReturnsFalseWhenChatIsNotOpened() {
        long chatId = 12345L;

        assertThat(userDao.checkChat(chatId)).isFalse();
    }

    @Test
    void testCheckChatReturnsTrueWhenChatIsOpened() {
        long chatId = 12345L;
        userDao.openChat(chatId);

        assertThat(userDao.checkChat(chatId)).isTrue();
    }

    @Test
    void testOpenChatCreatesIncomeAndExpenseLists() {
        long chatId = 12345L;
        userDao.openChat(chatId);

        assertDoesNotThrow(() -> incomeDao.getIncomes(chatId));
        assertDoesNotThrow(() -> expenseDao.getExpenses(chatId));
    }


}

