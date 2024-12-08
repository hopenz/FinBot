package ru.naumen.bot.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.dao.provider.BalanceDaoProvider;
import ru.naumen.bot.data.dao.provider.IncomeDaoProvider;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.exception.DaoException;

import java.time.LocalDate;
import java.util.List;

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
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345L;

    /**
     * Инициализация всех зависимостей и {@link IncomeService} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        IncomeDaoProvider incomeDaoProviderMock = Mockito.mock(IncomeDaoProvider.class);
        BalanceDaoProvider balanceDaoProviderMock = Mockito.mock(BalanceDaoProvider.class);
        incomeDaoMock = Mockito.mock(IncomeDao.class);
        balanceDaoMock = Mockito.mock(BalanceDao.class);
        Mockito.when(incomeDaoProviderMock.getIncomeDaoForUser(chatId)).thenReturn(incomeDaoMock);
        Mockito.when(balanceDaoProviderMock.getBalanceDaoForUser(chatId)).thenReturn(balanceDaoMock);
        incomeService = new IncomeService(incomeDaoProviderMock, balanceDaoProviderMock);
    }

    /**
     * Тест для проверки метода {@link IncomeService#getIncomes}.
     * Проверяет, что метод возвращает корректный список доходов.
     */
    @Test
    void testGetIncomes() throws DaoException {
        List<Income> expectedIncomes = List.of(
                new Income("Доход 1", 50.0, LocalDate.now()),
                new Income("Доход 2", 20.0, LocalDate.now())
        );
        Mockito.when(incomeService.getIncomes(chatId)).thenReturn(expectedIncomes);

        List<Income> actualIncomes = incomeService.getIncomes(chatId);

        Mockito.verify(incomeDaoMock).getIncomes(chatId);
        Assertions.assertThat(expectedIncomes).isEqualTo(actualIncomes);
    }

    /**
     * Тест для проверки метода {@link IncomeService#addIncome}.
     * Проверяет, что доход добавляется в хранилище и баланс обновляется корректно.
     */
    @Test
    void testAddIncome() throws DaoException {
        String incomeMessage = "+ 30 Доход 1";
        Mockito.when(balanceDaoMock.getBalance(chatId)).thenReturn(100.0);

        incomeService.addIncome(incomeMessage, chatId);

        Income expectedIncome = new Income("Доход 1", 30.0, LocalDate.now());
        Mockito.verify(incomeDaoMock).addIncome(chatId, expectedIncome);
        Mockito.verify(balanceDaoMock).setBalance(chatId, 130.0);
    }

    /**
     * Тест для проверки метода {@link IncomeService#addIncomes}.
     * Проверяет, что список доходов добавляется в хранилище.
     */
    @Test
    void testAddIncomes() throws DaoException {
        List<Income> incomes = List.of(
                new Income("Доход 1", 50.0, LocalDate.now()),
                new Income("Доход 2", 20.0, LocalDate.now())
        );

        incomeService.addIncomes(chatId, incomes);

        Mockito.verify(incomeDaoMock).addIncomes(chatId, incomes);
    }

    /**
     * Тест для проверки метода {@link ExpenseService#removeExpenses}.
     * Проверяет, что доходы удаляются из хранилища.
     */
    @Test
    void testRemoveIncomes() throws DaoException {
        incomeService.removeIncomes(chatId);

        Mockito.verify(incomeDaoMock).removeIncomes(chatId);
    }
}
