package ru.naumen.bot.data.dao;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.inMemory.InMemoryBalanceDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryExpenseDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryIncomeDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryUserDao;
import ru.naumen.bot.data.entity.DataType;

/**
 * Класс DaoProvider предоставляет доступ к DAO-объектам для работы с данными пользователей.
 */
@Component
public class DaoProvider {

    /**
     * DAO для управления данными о пользователях в памяти.
     */
    private final InMemoryUserDao inMemoryUserDao;

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
     * @param inMemoryUserDao    DAO для управления пользователями.
     * @param inMemoryBalanceDao DAO для управления балансом.
     * @param inMemoryExpenseDao DAO для управления расходами.
     * @param inMemoryIncomeDao  DAO для управления доходами.
     */
    public DaoProvider(InMemoryUserDao inMemoryUserDao, InMemoryBalanceDao inMemoryBalanceDao,
                       InMemoryExpenseDao inMemoryExpenseDao, InMemoryIncomeDao inMemoryIncomeDao) {
        this.inMemoryUserDao = inMemoryUserDao;
        this.inMemoryBalanceDao = inMemoryBalanceDao;
        this.inMemoryExpenseDao = inMemoryExpenseDao;
        this.inMemoryIncomeDao = inMemoryIncomeDao;
    }

    /**
     * Возвращает DAO для работы с балансом пользователя для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить DAO
     * @return InMemoryBalanceDao, если DataType пользователя равен IN_MEMORY, иначе .
     */
    public BalanceDao getBalanceDaoForUser(long chatId) {
        return inMemoryUserDao.getDataType(chatId).equals(DataType.IN_MEMORY)
                ? inMemoryBalanceDao
                : null;

    }

    /**
     * Возвращает DAO для работы с доходами пользователя для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить DAO
     * @return InMemoryIncomeDao, если DataType пользователя равен IN_MEMORY, иначе .
     */
    public IncomeDao getIncomeDaoForUser(long chatId) {
        return inMemoryUserDao.getDataType(chatId).equals(DataType.IN_MEMORY)
                ? inMemoryIncomeDao
                : null;
    }

    /**
     * Возвращает DAO для работы с расходами пользователя для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить DAO
     * @return InMemoryExpenseDao, если DataType пользователя равен IN_MEMORY, иначе .
     */
    public ExpenseDao getExpenseDaoForUser(long chatId) {
        return inMemoryUserDao.getDataType(chatId).equals(DataType.IN_MEMORY)
                ? inMemoryExpenseDao
                : null;
    }

    /**
     * Возвращает DAO для работы с балансом пользователя в памяти.
     *
     * @return InMemoryBalanceDao для работы с балансом в памяти
     */
    public InMemoryBalanceDao getInMemoryBalanceDao() {
        return inMemoryBalanceDao;
    }

    /**
     * Возвращает DAO для работы с расходами пользователя в памяти.
     *
     * @return InMemoryExpenseDao для работы с пользователями в памяти
     */
    public InMemoryExpenseDao getInMemoryExpenseDao() {
        return inMemoryExpenseDao;
    }

    /**
     * Возвращает DAO для работы с доходами пользователя в памяти.
     *
     * @return InMemoryIncomeDao для работы с доходами в памяти
     */
    public InMemoryIncomeDao getInMemoryIncomeDao() {
        return inMemoryIncomeDao;
    }
}
