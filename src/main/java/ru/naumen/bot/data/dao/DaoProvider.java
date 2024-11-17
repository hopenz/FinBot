package ru.naumen.bot.data.dao;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.googleSheets.GoogleSheetsBalanceDao;
import ru.naumen.bot.data.dao.googleSheets.GoogleSheetsExpenseDao;
import ru.naumen.bot.data.dao.googleSheets.GoogleSheetsIncomeDao;
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
     * DAO для управления балансом пользователя в гугл таблице
     */
    private final GoogleSheetsIncomeDao googleSheetsIncomeDao;

    /**
     * DAO для управления расходами пользователя в гугл таблице
     */
    private final GoogleSheetsExpenseDao googleSheetsExpenseDao;

    /**
     * DAO для управления балансом пользователя в гугл таблице
     */
    private final GoogleSheetsBalanceDao googleSheetsBalanceDao;

    /**
     * Конструктор для инициализации всех зависимостей DAO.
     *
     * @param inMemoryUserDao        DAO для управления пользователями.
     * @param inMemoryBalanceDao     DAO для управления балансом.
     * @param inMemoryExpenseDao     DAO для управления расходами.
     * @param inMemoryIncomeDao      DAO для управления доходами.
     * @param googleSheetsIncomeDao  DAO для управления доходами в гугл таблице
     * @param googleSheetsExpenseDao DAO для управления расходами в гугл таблице
     * @param googleSheetsBalanceDao DAO для управления балансом в гугл таблице
     */
    public DaoProvider(InMemoryUserDao inMemoryUserDao, InMemoryBalanceDao inMemoryBalanceDao,
                       InMemoryExpenseDao inMemoryExpenseDao, InMemoryIncomeDao inMemoryIncomeDao,
                       GoogleSheetsIncomeDao googleSheetsIncomeDao, GoogleSheetsExpenseDao googleSheetsExpenseDao,
                       GoogleSheetsBalanceDao googleSheetsBalanceDao) {
        this.inMemoryUserDao = inMemoryUserDao;
        this.inMemoryBalanceDao = inMemoryBalanceDao;
        this.inMemoryExpenseDao = inMemoryExpenseDao;
        this.inMemoryIncomeDao = inMemoryIncomeDao;
        this.googleSheetsIncomeDao = googleSheetsIncomeDao;
        this.googleSheetsExpenseDao = googleSheetsExpenseDao;
        this.googleSheetsBalanceDao = googleSheetsBalanceDao;
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
                : googleSheetsBalanceDao;

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
                : googleSheetsIncomeDao;
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
                : googleSheetsExpenseDao;
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
