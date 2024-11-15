package ru.naumen.bot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.dao.DaoProvider;
import ru.naumen.bot.data.dao.inMemory.InMemoryBalanceDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryExpenseDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryIncomeDao;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;

import java.time.LocalDate;
import java.util.List;

/**
 * Тесты для класса {@link DatabaseService}, который предоставляет методы для работы с базами данных.
 */
public class DatabaseServiceTest {

    private DaoProvider daoProvider;
    private UserService userService;
    private IncomeService incomeService;
    private ExpenseService expenseService;
    private BalanceService balanceService;
    private DatabaseService databaseService;

    /**
     * Настройка зависимостей перед выполнением каждого теста.
     */
    @BeforeEach
    void setUp() {
        daoProvider = Mockito.mock(DaoProvider.class);
        userService = Mockito.mock(UserService.class);
        incomeService = Mockito.mock(IncomeService.class);
        expenseService = Mockito.mock(ExpenseService.class);
        balanceService = Mockito.mock(BalanceService.class);

        databaseService = new DatabaseService(daoProvider, userService, incomeService, expenseService, balanceService);
    }


    /**
     * Тест для метода {@link DatabaseService#changeDB(long, DataType)}.
     * <p>
     * Проверяет:
     * <ul>
     *     <li>Правильность вызова методов для получения текущих данных (доходы, расходы, баланс).</li>
     *     <li>Удаление данных из старой базы.</li>
     *     <li>Установку нового типа базы данных.</li>
     *     <li>Создание структуры новой базы не происходит, так как новый тип данных - GoogleSheet.</li>
     *     <li>Запись данных в новую базу.</li>
     * </ul>
     */
    @Test
    void testChangeDBWithNewTypeGoogleSheet() {
        long chatId = 12345L;
        DataType newDataType = DataType.IN_GOOGLE_SHEET;
        List<Income> mockIncomes = List.of(new Income("Доход 1", 20.0, LocalDate.now()));
        List<Expense> mockExpenses = List.of(new Expense("Расход 1", 30.0, LocalDate.now()));
        Double mockBalance = 100.0;

        Mockito.when(incomeService.getIncomes(chatId)).thenReturn(mockIncomes);
        Mockito.when(expenseService.getExpenses(chatId)).thenReturn(mockExpenses);
        Mockito.when(balanceService.getBalance(chatId)).thenReturn(mockBalance);

        databaseService.changeDB(chatId, newDataType);

        Mockito.verify(incomeService).getIncomes(chatId);
        Mockito.verify(expenseService).getExpenses(chatId);
        Mockito.verify(balanceService).getBalance(chatId);

        Mockito.verify(incomeService).removeIncomes(chatId);
        Mockito.verify(expenseService).removeExpenses(chatId);
        Mockito.verify(balanceService).removeBalance(chatId);

        Mockito.verify(userService).setDataType(chatId, newDataType);
        Mockito.verifyNoInteractions(daoProvider);

        Mockito.verify(incomeService).addIncomes(chatId, mockIncomes);
        Mockito.verify(expenseService).addExpenses(chatId, mockExpenses);
        Mockito.verify(balanceService).setBalance(chatId, mockBalance);
    }

    /**
     * Тест для метода {@link DatabaseService#changeDB(long, DataType)}.
     * <p>
     * Проверяет: Создание структуры новой базы, если она создается в памяти.
     */
    @Test
    void testChangeDBWithNewTypeInMemory() {
        long chatId = 12345L;
        DataType newDataType = DataType.IN_MEMORY;
        InMemoryIncomeDao inMemoryIncomeDao = Mockito.mock(InMemoryIncomeDao.class);
        InMemoryExpenseDao inMemoryExpenseDao = Mockito.mock(InMemoryExpenseDao.class);
        InMemoryBalanceDao inMemoryBalanceDao = Mockito.mock(InMemoryBalanceDao.class);

        Mockito.when(daoProvider.getInMemoryIncomeDao()).thenReturn(inMemoryIncomeDao);
        Mockito.when(daoProvider.getInMemoryExpenseDao()).thenReturn(inMemoryExpenseDao);
        Mockito.when(daoProvider.getInMemoryBalanceDao()).thenReturn(inMemoryBalanceDao);

        databaseService.changeDB(chatId, newDataType);

        Mockito.verify(daoProvider).getInMemoryBalanceDao();
        Mockito.verify(daoProvider).getInMemoryIncomeDao();
        Mockito.verify(daoProvider).getInMemoryExpenseDao();

        Mockito.verify(inMemoryBalanceDao).setBalance(chatId, 0.0);
        Mockito.verify(inMemoryIncomeDao).createUserList(chatId);
        Mockito.verify(inMemoryExpenseDao).createUserList(chatId);
    }

}
