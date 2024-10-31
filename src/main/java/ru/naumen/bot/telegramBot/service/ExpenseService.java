package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Update;
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
     * Получает список расходов пользователя на основе информации из обновления Telegram.
     *
     * @param update обновление от Telegram.
     * @return список объектов {@link Expense}, представляющих расходы пользователя.
     */
    public List<Expense> getExpenses(Update update) {
        return expenseDao.getExpenses(update.message().chat().id());
    }

    /**
     * Добавляет расходы в хранилище и обнавляет баланс.
     *
     * @param expense сообщение от пользователя.
     * @param update  обновление от Telegrem.
     */
    public void addExpense(String expense, Update update) {
        String[] arrayOfStringExpense = expense.split(" ", 3);
        long chatId = update.message().chat().id();
        Expense newExpense = new Expense(
                arrayOfStringExpense[2], Double.parseDouble(arrayOfStringExpense[1]), LocalDate.now());
        expenseDao.addExpense(chatId, newExpense);
        balanceDao.setBalance(chatId,
                balanceDao.getBalance(chatId) - Double.parseDouble(arrayOfStringExpense[1]));
    }
}
