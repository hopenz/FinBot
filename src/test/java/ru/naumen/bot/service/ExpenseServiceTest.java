package ru.naumen.bot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.entity.Expense;

import java.time.LocalDate;

/**
 * Тесты для класса {@link ExpenseService}, проверяющие корректность обработки расходов.
 */
public class ExpenseServiceTest {

    /**
     * Мок-объект для {@link ExpenseDao}, используемый для работы с расходами пользователей.
     */
    private ExpenseDao expenseDaoMock;

    /**
     * Мок-объект для {@link BalanceDao}, используемый для управления балансом пользователей.
     */
    private BalanceDao balanceDaoMock;

    /**
     * Тестируемый объект {@link ExpenseService}, который проверяется в данном тестовом классе.
     */
    private ExpenseService expenseService;

    /**
     * Инициализация всех зависимостей и {@link ExpenseService} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        expenseDaoMock = Mockito.mock(ExpenseDao.class);
        balanceDaoMock = Mockito.mock(BalanceDao.class);
        expenseService = new ExpenseService(expenseDaoMock, balanceDaoMock);
    }

    /**
     * Тест для добавления расхода. Проверяет, что метод {@link ExpenseDao#addExpense(long, Expense)}
     * вызывается с правильными параметрами, и что баланс обновляется корректно.
     */
    @Test
    void testAddExpense() {
        String expense = "- 100000 покупка сумки";
        expenseService.addExpense(expense, 12345L);

        Mockito.verify(expenseDaoMock).addExpense(12345L, new Expense("покупка сумки",
                100000.0, LocalDate.now()));
        Mockito.verify(balanceDaoMock).setBalance(12345L, -100000.0);
    }
}