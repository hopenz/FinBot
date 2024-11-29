package ru.naumen.bot.data.dao.inMemory;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.UserDao;

/**
 * Компонент, объединяющий DAO для операций в оперативной памяти.
 */
@Component
public class InMemoryDaoGroup {

    /**
     * DAO для управления данными о пользователях в памяти.
     */
    private final UserDao userDao;

    /**
     * DAO для управления балансом пользователя в памяти.
     */
    private final InMemoryBalanceDao inMemoryBalanceDao;

    /**
     * DAO для управления расходами пользователя в памяти.
     */
    private final InMemoryExpenseDao inMemoryExpenseDao;

    /**
     * DAO для управления доходами пользователя в памяти.
     */
    private final InMemoryIncomeDao inMemoryIncomeDao;

    /**
     * Конструктор для инициализации всех зависимостей DAO.
     *
     * @param userDao    DAO для управления пользователями.
     * @param inMemoryBalanceDao DAO для управления балансом.
     * @param inMemoryExpenseDao DAO для управления расходами.
     * @param inMemoryIncomeDao  DAO для управления доходами.
     */
    public InMemoryDaoGroup(UserDao userDao, InMemoryBalanceDao inMemoryBalanceDao,
                            InMemoryExpenseDao inMemoryExpenseDao, InMemoryIncomeDao inMemoryIncomeDao) {
        this.userDao = userDao;
        this.inMemoryBalanceDao = inMemoryBalanceDao;
        this.inMemoryExpenseDao = inMemoryExpenseDao;
        this.inMemoryIncomeDao = inMemoryIncomeDao;
    }

    /**
     * Возвращает DAO для управления данными о пользователях в памяти.
     */
    public UserDao getInMemoryUserDao() {
        return userDao;
    }

    /**
     * Возвращает DAO для управления балансом пользователя в памяти.
     */
    public InMemoryBalanceDao getInMemoryBalanceDao() {
        return inMemoryBalanceDao;
    }

    /**
     * Возвращает DAO для управления расходами пользователя в памяти.
     */
    public InMemoryExpenseDao getInMemoryExpenseDao() {
        return inMemoryExpenseDao;
    }

    /**
     * Возвращает DAO для управления доходами пользователя в памяти.
     */
    public InMemoryIncomeDao getInMemoryIncomeDao() {
        return inMemoryIncomeDao;
    }
}
