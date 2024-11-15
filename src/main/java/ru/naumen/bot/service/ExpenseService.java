package ru.naumen.bot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.DaoProvider;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.entity.Expense;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис IncomeService предоставляет методы для работы с расходами пользователя.
 */
@Service
public class ExpenseService {

    /**
     * Класс, предоставляющий доступ к DAO-объектам для работы с данными пользователей.
     */
    private final DaoProvider daoProvider;

    /**
     * Конструктор класса ExpenseService.
     *
     * @param daoProvider объект, предоставляющий доступ к DAO-объектам для работы с данными пользователей.
     */
    public ExpenseService(DaoProvider daoProvider) {
        this.daoProvider = daoProvider;
    }

    /**
     * Получает список расходов пользователя на основе chatId пользователя.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     * @return список объектов {@link Expense}, представляющих расходы пользователя.
     */
    public List<Expense> getExpenses(long chatId) {
        ExpenseDao expenseDao = daoProvider.getExpenseDaoForUser(chatId);
        return expenseDao.getExpenses(chatId);
    }

    /**
     * Добавляет расход в хранилище и обновляет баланс.
     *
     * @param expense сообщение от пользователя.
     * @param chatId  идентификатор чата, в котором было отправлено сообщение
     */
    public void addExpense(String expense, long chatId) {
        ExpenseDao expenseDao = daoProvider.getExpenseDaoForUser(chatId);
        BalanceDao balanceDao = daoProvider.getBalanceDaoForUser(chatId);
        String[] arrayOfStringExpense = expense.split(" ", 3);
        Expense newExpense = new Expense(
                arrayOfStringExpense[2], Double.parseDouble(arrayOfStringExpense[1]), LocalDate.now());
        expenseDao.addExpense(chatId, newExpense);
        balanceDao.setBalance(chatId,
                balanceDao.getBalance(chatId) - Double.parseDouble(arrayOfStringExpense[1]));
    }

    /**
     * Добавляет список расходов в хранилище и обновляет баланс.
     *
     * @param chatId   идентификатор чата, в котором было отправлено сообщение
     * @param expenses список объектов {@link Expense}, представляющих расходы пользователя.
     */
    public void addExpenses(long chatId, List<Expense> expenses) {
        ExpenseDao expenseDao = daoProvider.getExpenseDaoForUser(chatId);
        expenseDao.addExpenses(chatId, expenses);
    }

    /**
     * Удаляет расходы из хранилища.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     */
    public void removeExpenses(long chatId) {
        ExpenseDao expenseDao = daoProvider.getExpenseDaoForUser(chatId);
        expenseDao.removeExpenses(chatId);
    }
}
