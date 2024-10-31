package ru.naumen.bot.telegramBot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.entity.Income;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Тесты для класса {@link IncomeService}, проверяющие корректность обработки доходов.
 */
public class IncomeServiceTest {

    /**
     * Мок-объект для {@link IncomeDao}, используемый для работы с доходами пользователей.
     */
    private IncomeDao incomeDaoMock;

    /**
     * Мок-объект для {@link BalanceDao}, используемый для управления балансом пользователей.
     */
    private BalanceDao balanceDaoMock;

    /**
     * Тестируемый объект {@link IncomeService}, который проверяется в данном тестовом классе.
     */
    private IncomeService incomeService;

    /**
     * Инициализация всех зависимостей и {@link IncomeService} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        incomeDaoMock = mock(IncomeDao.class);
        balanceDaoMock = mock(BalanceDao.class);
        incomeService = new IncomeService(incomeDaoMock, balanceDaoMock);
    }

    /**
     * Тест для добавления дохода. Проверяет, что метод {@link IncomeDao#addIncome(long, Income)}
     * вызывается с правильными параметрами, и что баланс обновляется корректно.
     */
    @Test
    void testAddIncome() {
        String income = "+ 100000 зарплата";
        incomeService.addIncome(income, 12345L);

        verify(incomeDaoMock).addIncome(12345L, new Income("зарплата", 100000.0, LocalDate.now()));
        verify(balanceDaoMock).setBalance(12345L, 100000.0);
    }
}
