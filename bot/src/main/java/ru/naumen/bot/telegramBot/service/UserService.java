package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryExpenseDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryIncomeDao;

/**
 * Сервис UserService предоставляет методы для работы с данными пользователей.
 * Он включает в себя функционал для проверки и открытия чатов.
 */
@Service
public class UserService {

    /**
     * DAO для работы с пользователями.
     */
    private final UserDao userDao;

    /**
     * DAO для работы с данными о доходах.
     */
    private final InMemoryIncomeDao incomeDao;

    /**
     * DAO для работы с данными о расходах.
     */
    private final InMemoryExpenseDao expenseDao;

    /**
     * Dao для работы с балансом.
     */
    private final BalanceDao balanceDao;

    /**
     * Конструктор UserService. Инициализирует сервис с объектами DAO.
     *
     * @param userDao    DAO для работы с пользователями.
     * @param incomeDao  DAO для работы с доходами.
     * @param expenseDao DAO для работы с расходами.
     * @param balanceDao DAO для работы с балансом.
     */
    @Autowired
    public UserService(UserDao userDao, InMemoryIncomeDao incomeDao, InMemoryExpenseDao expenseDao, BalanceDao balanceDao) {
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
        if (!userDao.checkChat(chatId)) {
            userDao.openChat(chatId);
            incomeDao.createUserList(chatId);
            expenseDao.createUserList(chatId);
            balanceDao.setBalance(chatId, 0.0);
        }
    }


}
