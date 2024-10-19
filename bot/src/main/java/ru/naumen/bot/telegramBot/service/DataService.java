package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;

import java.util.List;

/**
 * Сервис DataService предоставляет методы для работы с данными пользователей, доходами и расходами.
 * Он включает функционал для проверки и открытия чатов, а также получения данных о доходах и расходах.
 */
@Service
public class DataService {

    /**
     * DAO для работы с пользователями.
     */
    private final UserDao userDao;

    /**
     * DAO для работы с данными о доходах.
     */
    private final IncomeDao incomeDao;

    /**
     * DAO для работы с данными о расходах.
     */
    private final ExpenseDao expenseDao;

    /**
     * Dao для работы с балансом.
     */
    private final BalanceDao balanceDao;

    /**
     * Конструктор DataService. Инициализирует сервис с объектами DAO.
     *
     * @param userDao DAO для работы с пользователями.
     * @param incomeDao DAO для работы с доходами.
     * @param expenseDao DAO для работы с расходами.
     */
    @Autowired
    public DataService(UserDao userDao, IncomeDao incomeDao, ExpenseDao expenseDao, BalanceDao balanceDao) {
        this.userDao = userDao;
        this.incomeDao = incomeDao;
        this.expenseDao = expenseDao;
        this.balanceDao = balanceDao;
    }

    /**
     * Проверяет, открыт ли чат для текущего пользователя.
     *
     * @param update обновление от Telegram, содержащее информацию о чате.
     * @return true, если чат открыт, иначе false.
     */
    public boolean isChatOpened(Update update) {
        long chatId = update.message().chat().id();
        return userDao.checkChat(chatId);
    }

    /**
     * Открывает чат для текущего пользователя, если он еще не был открыт.
     *
     * @param update обновление от Telegram.
     */
    public void openChat(Update update) {
        long chatId = update.message().chat().id();
        userDao.openChat(chatId);
    }

    /**
     * Получает список доходов пользователя на основе информации из обновления Telegram.
     *
     * @param update обновление от Telegram.
     * @return список объектов {@link Income}, представляющих доходы пользователя.
     */
    public List<Income> getIncomes(Update update) {
        return incomeDao.getIncomes(update.message().chat().id());
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
     * Получает баланс пользователя на основе информации из обновления Telegram
     * @return
     */
    public double getBalance(Update update) {
        return balanceDao.getBalance(update.message().chat().id());
    }
}
