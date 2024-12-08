package ru.naumen.bot.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.dao.LimitDao;
import ru.naumen.bot.data.dao.provider.BalanceDaoProvider;
import ru.naumen.bot.data.dao.provider.ExpenseDaoProvider;
import ru.naumen.bot.data.dao.provider.LimitDaoProvider;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.ExpenseCategory;
import ru.naumen.bot.data.entity.Limit;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.exception.ExceedingTheLimitException;

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
     * Мок-объект для {@link LimitDao}, используемый для работы с лимитом пользователя.
     */
    private LimitDao limitDaoMock;

    /**
     * Инициализация всех зависимостей и {@link ExpenseService} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        BalanceDaoProvider balanceDaoProviderMock = Mockito.mock(BalanceDaoProvider.class);
        ExpenseDaoProvider expenseDaoProviderMock = Mockito.mock(ExpenseDaoProvider.class);
        LimitDaoProvider limitDaoProviderMock = Mockito.mock(LimitDaoProvider.class);
        UserService userServiceMock = Mockito.mock(UserService.class);
        limitDaoMock = Mockito.mock(LimitDao.class);
        expenseDaoMock = Mockito.mock(ExpenseDao.class);
        balanceDaoMock = Mockito.mock(BalanceDao.class);
        Mockito.when(expenseDaoProviderMock.getExpenseDaoForUser(chatId)).thenReturn(expenseDaoMock);
        Mockito.when(balanceDaoProviderMock.getBalanceDaoForUser(chatId)).thenReturn(balanceDaoMock);
        Mockito.when(limitDaoProviderMock.getLimitDaoForUser(chatId)).thenReturn(limitDaoMock);
        expenseService = new ExpenseService(expenseDaoProviderMock, limitDaoProviderMock,
                balanceDaoProviderMock, userServiceMock);
    }

    /**
     * Тест для проверки метода {@link ExpenseService#getExpenses}.
     * Проверяет, что метод возвращает корректный список расходов.
     */
    @Test
    void testGetExpenses() throws DaoException {
        List<Expense> expectedExpenses = List.of(
                new Expense("Расход 1", 50.0, ExpenseCategory.OTHER, LocalDate.now()),
                new Expense("Расход 2", 20.0, ExpenseCategory.OTHER, LocalDate.now())
        );
        Mockito.when(expenseDaoMock.getExpenses(chatId)).thenReturn(expectedExpenses);

        List<Expense> actualExpenses = expenseService.getExpenses(chatId);

        Mockito.verify(expenseDaoMock).getExpenses(chatId);
        Assertions.assertThat(expectedExpenses).isEqualTo(actualExpenses);
    }

    /**
     * Тест для проверки метода {@link ExpenseService#addExpense}.
     * Проверяет, что расход добавляется в хранилище и баланс, и лимит обновляются корректно.
     */
    @Test
    void testAddExpense() throws DaoException, ExceedingTheLimitException {
        String expenseMessage = "- 30 Расход 1";
        Limit limit = new Limit(100.0, 0.0);
        Mockito.when(balanceDaoMock.getBalance(chatId)).thenReturn(100.0);
        Mockito.when(limitDaoMock.getLimit(chatId)).thenReturn(limit);

        expenseService.addExpense(expenseMessage, chatId);

        Expense expectedExpense =
                new Expense("Расход 1", 30.0, ExpenseCategory.OTHER, LocalDate.now());
        Mockito.verify(expenseDaoMock).addExpense(chatId, expectedExpense);
        Mockito.verify(balanceDaoMock).setBalance(chatId, 70.0);
        Mockito.verify(limitDaoMock).setLimit(Mockito.eq(chatId), Mockito.argThat(limit1 ->
                limit1.getDailyExpensesSum() == 30.0 && limit1.getDailyLimit() == 100.0));
    }

    /**
     * Тест для проверки метода {@link ExpenseService#addExpense}.
     * Проверяет, что расход добавляется в хранилище и баланс обновляется корректно при отсутствии лимита.
     */
    @Test
    void testAddExpenseWithNullLimit() throws DaoException, ExceedingTheLimitException {
        String expenseMessage = "- 30 Расход 1";
        Mockito.when(balanceDaoMock.getBalance(chatId)).thenReturn(100.0);
        Mockito.when(limitDaoMock.getLimit(chatId)).thenReturn(null);

        expenseService.addExpense(expenseMessage, chatId);

        Expense expectedExpense =
                new Expense("Расход 1", 30.0, ExpenseCategory.OTHER, LocalDate.now());
        Mockito.verify(expenseDaoMock).addExpense(chatId, expectedExpense);
        Mockito.verify(balanceDaoMock).setBalance(chatId, 70.0);
        Mockito.verify(limitDaoMock).getLimit(chatId);
        Mockito.verifyNoMoreInteractions(limitDaoMock);
    }

    /**
     * Тест для проверки метода {@link ExpenseService#addExpenses}.
     * Проверяет, что список расходов добавляется в хранилище.
     */
    @Test
    void testAddExpenses() throws DaoException {
        List<Expense> expenses = List.of(
                new Expense("Расход 1", 50.0, ExpenseCategory.OTHER, LocalDate.now()),
                new Expense("Расход 2", 20.0, ExpenseCategory.OTHER, LocalDate.now())
        );

        expenseService.addExpenses(chatId, expenses);

        Mockito.verify(expenseDaoMock).addExpenses(chatId, expenses);
    }

    /**
     * Тест для проверки метода {@link ExpenseService#removeExpenses}.
     * Проверяет, что расходы удаляются из хранилища.
     */
    @Test
    void testRemoveExpenses() throws DaoException {
        expenseService.removeExpenses(chatId);

        Mockito.verify(expenseDaoMock).removeExpenses(chatId);
    }

    /**
     * Тест для проверки метода {@link ExpenseService#setExpensesLimit}.
     * Проверяет, что метод установки лимита работает корректно
     */
    @Test
    void testSetExpensesLimitWithValidLimit() throws DaoException {
        expenseService.setExpensesLimit(chatId, "100.0");

        Mockito.verify(limitDaoMock).setLimit(Mockito.eq(chatId), Mockito.argThat(limit ->
                limit.getDailyLimit() == 100.0 && limit.getDailyExpensesSum() == 0.0));
    }

    /**
     * Тест для проверки метода {@link ExpenseService#setExpensesLimit}.
     * Проверяет, что метод установки лимита выбрасывает исключение при невалидном лимите
     */
    @Test
    void testSetExpensesLimitWithInValidLimit() {
        Assertions.assertThatThrownBy(
                        () -> expenseService.setExpensesLimit(chatId, "-100")).
                isInstanceOf(IllegalArgumentException.class).
                hasMessage("Лимит не может быть отрицательным или нулём");
        Mockito.verifyNoInteractions(limitDaoMock);
    }
}
