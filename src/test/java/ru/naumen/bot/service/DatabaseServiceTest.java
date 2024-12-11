package ru.naumen.bot.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.data.entity.Limit;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.data.enums.DataType;
import ru.naumen.bot.data.enums.ExpenseCategory;
import ru.naumen.bot.exception.DaoException;

import java.time.LocalDate;
import java.util.List;

/**
 * Тесты для класса {@link DatabaseService}, который предоставляет методы для работы с базами данных.
 */
public class DatabaseServiceTest {

    /**
     * Мок-объект для {@link UserService}, используемый для проверки статуса чата.
     */
    private final UserService userService = Mockito.mock(UserService.class);

    /**
     * Мок-объект для {@link IncomeService}, используемый для работы с доходами пользователя.
     */
    private final IncomeService incomeService = Mockito.mock(IncomeService.class);

    /**
     * Мок-объект для {@link ExpenseService}, используемый для работы с расходами пользователя.
     */
    private final ExpenseService expenseService = Mockito.mock(ExpenseService.class);

    /**
     * Мок-объект для {@link BalanceService}, используемый для управления балансом пользователей.
     */
    private final BalanceService balanceService = Mockito.mock(BalanceService.class);

    /**
     * Мок-объект для {@link GoogleSheetsService}, используемый для работы с Google Sheets.
     */
    private final GoogleSheetsService googleSheetsService = Mockito.mock(GoogleSheetsService.class);

    /**
     * Тестируемый объект {@link DatabaseService}.
     */
    private final DatabaseService databaseService = new DatabaseService(userService, incomeService,
            expenseService, balanceService, googleSheetsService);


    /**
     * Тест для метода {@link DatabaseService#changeDB(long, DataType)}.
     * <p>
     * Проверяет:
     * <ul>
     *     <li>Правильность вызова методов для получения текущих данных (доходы, расходы, баланс, лимит).</li>
     *     <li>Удаление данных из старой базы.</li>
     *     <li>Установку нового типа базы данных.</li>
     *     <li>Инициализация Google Sheets не происходит, так как новый тип данных - IN_MEMORY.</li>
     *     <li>Запись данных в новую базу.</li>
     * </ul>
     */
    @Test
    void testChangeDBWithNewTypeInMemory() throws DaoException {
        long chatId = 12345L;
        DataType newDataType = DataType.IN_MEMORY;
        List<Income> mockIncomes = List.of(new Income("Доход 1", 20.0, LocalDate.now()));
        List<Expense> mockExpenses = List.of(
                new Expense("Расход 1", 30.0, ExpenseCategory.OTHER, LocalDate.now()));
        Double mockBalance = 100.0;
        Limit limit = new Limit(100.0, 0.0);

        Mockito.when(incomeService.getIncomes(chatId)).thenReturn(mockIncomes);
        Mockito.when(expenseService.getExpenses(chatId)).thenReturn(mockExpenses);
        Mockito.when(balanceService.getBalance(chatId)).thenReturn(mockBalance);
        Mockito.when(expenseService.getExpensesLimit(chatId)).thenReturn(limit);
        Mockito.when(userService.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_TYPE_DB_FOR_CHANGE_DB);

        databaseService.changeDB(chatId, newDataType);

        Mockito.verify(incomeService).getIncomes(chatId);
        Mockito.verify(expenseService).getExpenses(chatId);
        Mockito.verify(balanceService).getBalance(chatId);
        Mockito.verify(expenseService).getExpensesLimit(chatId);

        Mockito.verify(userService).setDataType(chatId, newDataType);
        Mockito.verifyNoInteractions(googleSheetsService);

        Mockito.verify(incomeService).removeIncomes(chatId);
        Mockito.verify(expenseService).removeExpenses(chatId);
        Mockito.verify(balanceService).removeBalance(chatId);
        Mockito.verify(expenseService).removeExpensesLimit(chatId);

        Mockito.verify(incomeService).addIncomes(chatId, mockIncomes);
        Mockito.verify(expenseService).addExpenses(chatId, mockExpenses);
        Mockito.verify(balanceService).setBalance(chatId, mockBalance);
        Mockito.verify(expenseService).setExpensesLimit(chatId, limit);
    }

    /**
     * Тест для метода {@link DatabaseService#changeDB(long, DataType)}.
     * Проверяет инициализацию Google Sheets, если данные впервые переносятся в Google Sheets.
     */
    @Test
    void testChangeDBWithNewTypeGoogleSheetAndInitTable() throws DaoException {
        long chatId = 12345L;
        DataType newDataType = DataType.IN_GOOGLE_SHEET;
        Mockito.when(userService.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);
        databaseService.changeDB(chatId, newDataType);
        Mockito.verify(googleSheetsService).initGoogleSheets(chatId);
    }

}
