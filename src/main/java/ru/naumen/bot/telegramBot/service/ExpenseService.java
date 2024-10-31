package ru.naumen.bot.telegramBot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;
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
     * DAO для работы с данными о расходах.
     */
    private final ExpenseDao expenseDao;

    /**
     * Dao для работы с балансом.
     */
    private final BalanceDao balanceDao;


    /**
     * Конструктор ExpenseService. Реализует сервис с объектами DAO.
     *
     * @param expenseDao DAO для работы с расходами.
     * @param balanceDao DAO для работы с балансом.
     */
    public ExpenseService(ExpenseDao expenseDao, BalanceDao balanceDao) {
        this.expenseDao = expenseDao;
        this.balanceDao = balanceDao;
    }

    /**
     * Получает список расходов пользователя на основе chatId пользователя.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     * @return список объектов {@link Expense}, представляющих расходы пользователя.
     */
    public List<Expense> getExpenses(long chatId) {
        return expenseDao.getExpenses(chatId);
    }

    /**
     * Добавляет расходы в хранилище и обнавляет баланс.
     *
     * @param expense сообщение от пользователя.
     * @param chatId  идентификатор чата, в котором было отправлено сообщение
     */
    public void addExpense(String expense, long chatId) {
        String[] arrayOfStringExpense = expense.split(" ", 3);
        Expense newExpense = new Expense(
                arrayOfStringExpense[2], Double.parseDouble(arrayOfStringExpense[1]), LocalDate.now());
        expenseDao.addExpense(chatId, newExpense);
        balanceDao.setBalance(chatId,
                balanceDao.getBalance(chatId) - Double.parseDouble(arrayOfStringExpense[1]));
    }
}
