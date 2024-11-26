package ru.naumen.bot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.exception.DaoException;

import java.util.List;

/**
 * Сервис IncomeService предоставляет методы для работы с данными о финансах пользователя.
 */
@Service
public class FinanceDataService {

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
     * Конструктор класса FinanceDataService.
     *
     * @param incomeService  сервис для работы с доходами пользователей
     * @param expenseService сервис для работы с расходами пользователей
     * @param balanceService сервис для работы с балансом пользователей
     */
    public FinanceDataService(IncomeService incomeService, ExpenseService expenseService, BalanceService balanceService) {
        this.incomeService = incomeService;
        this.expenseService = expenseService;
        this.balanceService = balanceService;
    }

    /**
     * Возвращает баланс пользователя на основе информации chatId пользователя.
     */
    public Double getBalance(long chatId) throws DaoException {
        return balanceService.getBalance(chatId);
    }

    /**
     * Получает список доходов пользователя на основе chatId пользователя.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     * @return список объектов {@link Income}, представляющих доходы пользователя.
     */
    public List<Income> getIncomes(long chatId) throws DaoException {
        return incomeService.getIncomes(chatId);
    }

    /**
     * Получает список расходов пользователя на основе chatId пользователя.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     * @return список объектов {@link Expense}, представляющих расходы пользователя.
     */
    public List<Expense> getExpenses(long chatId) throws DaoException {
        return expenseService.getExpenses(chatId);
    }

    /**
     * Добавляет расход в хранилище и обновляет баланс.
     *
     * @param message сообщение от пользователя.
     * @param chatId  идентификатор чата, в котором было отправлено сообщение
     */
    public void addExpense(String message, long chatId) throws DaoException {
        expenseService.addExpense(message, chatId);
    }

    /**
     * Добавляет доход в хранилище и обновляет баланс.
     *
     * @param message сообщение от пользователя.
     * @param chatId  идентификатор чата, в котором было отправлено сообщение
     */
    public void addIncome(String message, long chatId) throws DaoException {
        incomeService.addIncome(message, chatId);
    }
}
