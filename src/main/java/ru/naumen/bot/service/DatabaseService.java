package ru.naumen.bot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.entity.*;
import ru.naumen.bot.exception.DaoException;

import java.util.List;

/**
 * Сервис для взаимодействия с базами данных.
 */
@Service
public class DatabaseService {

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
     * Сервис GoogleSheetsService предоставляет методы для работы с Google Sheets.
     */
    private final GoogleSheetsService googleSheetsService;

    /**
     * Конструктор класса DatabaseService.
     *
     * @param userService         сервис для работы с данными пользователей
     * @param incomeService       сервис для работы с доходами пользователей
     * @param expenseService      сервис для работы с расходами пользователей
     * @param balanceService      сервис для работы с балансом пользователей
     * @param googleSheetsService сервис для работы с Google Sheets
     */
    public DatabaseService(UserService userService, IncomeService incomeService, ExpenseService expenseService,
                           BalanceService balanceService, GoogleSheetsService googleSheetsService) {
        this.userService = userService;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
        this.balanceService = balanceService;
        this.googleSheetsService = googleSheetsService;
    }

    /**
     * Метод для переноса данных из одной базы данных в другую.
     *
     * @param chatId      идентификатор чата
     * @param newDataType новый тип базы данных
     */
    public void changeDB(long chatId, DataType newDataType) throws DaoException {
        List<Income> incomes = incomeService.getIncomes(chatId);
        List<Expense> expenses = expenseService.getExpenses(chatId);
        Double balance = balanceService.getBalance(chatId);
        Limit limit = expenseService.getExpensesLimit(chatId);

        userService.setDataType(chatId, newDataType);

        if (userService.getUserState(chatId).equals(ChatState.WAITING_FOR_GOOGLE_SHEET_LINK)) {
            googleSheetsService.initGoogleSheets(chatId);
        }

        incomeService.removeIncomes(chatId);
        expenseService.removeExpenses(chatId);
        balanceService.removeBalance(chatId);
        expenseService.removeExpensesLimit(chatId);

        incomeService.addIncomes(chatId, incomes);
        expenseService.addExpenses(chatId, expenses);
        balanceService.setBalance(chatId, balance);
        expenseService.setExpensesLimit(chatId, limit);
    }
}
