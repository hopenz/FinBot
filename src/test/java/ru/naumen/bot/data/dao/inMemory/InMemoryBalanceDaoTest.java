package ru.naumen.bot.data.dao.inMemory;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Тесты для класса {@link InMemoryBalanceDao}, проверяющие корректность работы с балансом пользователей.
 */
public class InMemoryBalanceDaoTest {

    /**
     * Тестируемый объект {@link InMemoryBalanceDao}, который проверяется в данном тестовом классе.
     */
    private InMemoryBalanceDao balanceDao;

    /**
     * Инициализация {@link InMemoryBalanceDao} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        balanceDao = new InMemoryBalanceDao();
    }

    /**
     * Тест для установки и получения баланса. Проверяет, что баланс корректно обновляется и возвращается.
     */
    @Test
    void testSetBalanceAndGetBalance() {
        long chatId = 12345L;

        Assertions.assertThat(balanceDao.getBalance(chatId)).isEqualTo(0.0);
        balanceDao.setBalance(chatId, 1.1);

        Assertions.assertThat(balanceDao.getBalance(chatId)).isEqualTo(1.1);
    }
}
