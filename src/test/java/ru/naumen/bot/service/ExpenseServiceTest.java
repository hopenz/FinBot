package ru.naumen.bot.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.DaoProvider;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.entity.Expense;

import java.time.LocalDate;
import java.util.List;

/**
 * Тесты для класса {@link ExpenseService}, проверяющие корректность обработки расходов.
 */
public class ExpenseServiceTest {

    /**
     * Мок-объект для {@link ExpenseDao}, используемый для работы с расходами пользователей.
     */
    private ExpenseDao expenseDaoMock;

    /**
     * Мок-объект для {@link BalanceDao}, используемый для работы с балансами пользователей.
     */
    private BalanceDao balanceDaoMock;

    /**
     * Тестируемый объект {@link ExpenseService}, который проверяется в данном тестовом классе.
     */
    private ExpenseService expenseService;

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345L;

    /**
     * Инициализация всех зависимостей и {@link ExpenseService} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        DaoProvider daoProviderMock = Mockito.mock(DaoProvider.class);
        expenseDaoMock = Mockito.mock(ExpenseDao.class);
        balanceDaoMock = Mockito.mock(BalanceDao.class);
        Mockito.when(daoProviderMock.getExpenseDaoForUser(chatId)).thenReturn(expenseDaoMock);
        Mockito.when(daoProviderMock.getBalanceDaoForUser(chatId)).thenReturn(balanceDaoMock);
        expenseService = new ExpenseService(daoProviderMock);
    }

    /**
     * Тест для проверки метода {@link ExpenseService#getExpenses}.
     * Проверяет, что метод возвращает корректный список расходов.
     */
    @Test
    void testGetExpenses() {
        List<Expense> expectedExpenses = List.of(
                new Expense("Расход 1", 50.0, LocalDate.now()),
                new Expense("Расход 2", 20.0, LocalDate.now())
        );
        Mockito.when(expenseDaoMock.getExpenses(chatId)).thenReturn(expectedExpenses);

        List<Expense> actualExpenses = expenseService.getExpenses(chatId);

        Mockito.verify(expenseDaoMock).getExpenses(chatId);
        Assertions.assertThat(expectedExpenses).isEqualTo(actualExpenses);
    }

    /**
     * Тест для проверки метода {@link ExpenseService#addExpense}.
     * Проверяет, что расход добавляется в хранилище и баланс обновляется корректно.
     */
    @Test
    void testAddExpense() {
        String expenseMessage = "- 30 Расход 1";
        Mockito.when(balanceDaoMock.getBalance(chatId)).thenReturn(100.0);

        expenseService.addExpense(expenseMessage, chatId);

        Expense expectedExpense = new Expense("Расход 1", 30.0, LocalDate.now());
        Mockito.verify(expenseDaoMock).addExpense(chatId, expectedExpense);
        Mockito.verify(balanceDaoMock).setBalance(chatId, 70.0);
    }

    /**
     * Тест для проверки метода {@link ExpenseService#addExpenses}.
     * Проверяет, что список расходов добавляется в хранилище.
     */
    @Test
    void testAddExpenses() {
        List<Expense> expenses = List.of(
                new Expense("Расход 1", 50.0, LocalDate.now()),
                new Expense("Расход 2", 20.0, LocalDate.now())
        );

        expenseService.addExpenses(chatId, expenses);

        Mockito.verify(expenseDaoMock).addExpenses(chatId, expenses);
    }

    /**
     * Тест для проверки метода {@link ExpenseService#removeExpenses}.
     * Проверяет, что расходы удаляются из хранилища.
     */
    @Test
    void testRemoveExpenses() {
        expenseService.removeExpenses(chatId);

        Mockito.verify(expenseDaoMock).removeExpenses(chatId);
    }
}
