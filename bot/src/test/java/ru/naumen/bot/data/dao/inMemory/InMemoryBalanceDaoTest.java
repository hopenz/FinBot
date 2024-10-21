package ru.naumen.bot.data.dao.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryBalanceDaoTest {

    private InMemoryBalanceDao balanceDao;

    @BeforeEach
    void setUp() {
        balanceDao = new InMemoryBalanceDao();
    }

    @Test
    void testSetBalanceAndGetBalance() {
        long chatId = 12345L;

        assertThat(balanceDao.getBalance(chatId)).isNull();
        balanceDao.setBalance(chatId, 1.1);

        assertThat(balanceDao.getBalance(chatId)).isEqualTo(1.1);
    }
}
