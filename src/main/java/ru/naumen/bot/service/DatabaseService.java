package ru.naumen.bot.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.DaoProvider;
import ru.naumen.bot.data.dao.googleSheets.GoogleSheetsDao;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;

import java.util.List;

/**
 * Сервис для взаимодействия с базами данных.
 */
@Service
public class DatabaseService {

    /**
     * Класс, предоставляющий доступ к DAO-объектам для работы с данными пользователей.
     */
    private final DaoProvider daoProvider;

    /**
     * Сервис UserService предоставляет методы для работы с данными пользователей.
     */
    private final UserService userService;

    /**
     * Сервис IncomeService предоставляет методы для работы с доходами пользователя.
     */
    private final IncomeService incomeService;

    /**
     * Сервис ExpenseService предоставляет методы для работы с расходами пользователя.
     */
    private final ExpenseService expenseService;

    /**
     * Сервис BalanceService предоставляет методы для работы с балансом пользователя.
     */
    private final BalanceService balanceService;

    /**
     * DAO для работы с Google Sheets.
     */
    private final GoogleSheetsDao googleSheetsDao;

    /**
     * Конструктор класса DatabaseService.
     *
     * @param daoProvider     сервис для взаимодействия с базами данных
     * @param userService     сервис для работы с данными пользователей
     * @param incomeService   сервис для работы с доходами пользователей
     * @param expenseService  сервис для работы с расходами пользователей
     * @param balanceService  сервис для работы с балансом пользователей
     * @param googleSheetsDao DAO для работы с Google Sheets
     */
    public DatabaseService(DaoProvider daoProvider, @Lazy UserService userService, IncomeService incomeService,
                           ExpenseService expenseService, BalanceService balanceService, GoogleSheetsDao googleSheetsDao) {
        this.daoProvider = daoProvider;
        this.userService = userService;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
        this.balanceService = balanceService;
        this.googleSheetsDao = googleSheetsDao;
    }

    /**
     * Метод для переноса данных из одной базы данных в другую.
     *
     * @param chatId      идентификатор чата
     * @param newDataType новый тип базы данных
     */
    public void changeDB(long chatId, DataType newDataType) {
        List<Income> incomes = incomeService.getIncomes(chatId);
        List<Expense> expenses = expenseService.getExpenses(chatId);
        Double balance = balanceService.getBalance(chatId);

        userService.setDataType(chatId, newDataType);

        incomeService.removeIncomes(chatId);
        expenseService.removeExpenses(chatId);
        balanceService.removeBalance(chatId);

        if (newDataType.equals(DataType.IN_MEMORY)) {
            createInMemoryDB(chatId);
        }

        incomeService.addIncomes(chatId, incomes);
        expenseService.addExpenses(chatId, expenses);
        balanceService.setBalance(chatId, balance);
    }

    /**
     * Метод для создания базы данных в памяти.
     *
     * @param chatId идентификатор чата
     */
    public void createInMemoryDB(long chatId) {
        daoProvider.getInMemoryBalanceDao().setBalance(chatId, 0.0);
        daoProvider.getInMemoryIncomeDao().createUserList(chatId);
        daoProvider.getInMemoryExpenseDao().createUserList(chatId);
    }

    /**
     * Метод для создания базы данных в Google Sheets.
     *
     * @param chatId идентификатор чата
     */
    public void createGoogleSheetsDB(long chatId) {
        googleSheetsDao.initGoogleSheets(chatId);
    }
}
