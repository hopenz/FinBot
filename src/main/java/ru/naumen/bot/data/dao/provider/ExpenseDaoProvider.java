package ru.naumen.bot.data.dao.provider;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.dao.googleSheets.GoogleSheetsExpenseDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryExpenseDao;
import ru.naumen.bot.data.enums.DataType;

/**
 * Класс предоставляет доступ к DAO-объектам для работы с расходами пользователя.
 */
@Component
public class ExpenseDaoProvider {

    /**
     * Экземпляр DAO, использующий Google Sheets для хранения расходов.
     */
    private final GoogleSheetsExpenseDao googleSheetsExpenseDao;

    /**
     * Экземпляр DAO, использующий память для хранения расходов.
     */
    private final InMemoryExpenseDao inMemoryExpenseDao;

    /**
     * Экземпляр DAO для получения информации о пользователе.
     */
    private final UserDao userDao;

    /**
     * Конструктор класса {@link ExpenseDaoProvider}.
     *
     * @param googleSheetsExpenseDao Экземпляр DAO для работы с Google Sheets.
     * @param inMemoryExpenseDao     Экземпляр DAO для работы с памятью.
     * @param userDao                Экземпляр DAO для получения информации о пользователе.
     */
    public ExpenseDaoProvider(GoogleSheetsExpenseDao googleSheetsExpenseDao,
                              InMemoryExpenseDao inMemoryExpenseDao, UserDao userDao) {
        this.googleSheetsExpenseDao = googleSheetsExpenseDao;
        this.inMemoryExpenseDao = inMemoryExpenseDao;
        this.userDao = userDao;
    }

    /**
     * Возвращает DAO для работы с расходами пользователя для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить DAO
     * @return InMemoryExpenseDao, если DataType пользователя равен IN_MEMORY, иначе GoogleSheetsExpenseDao.
     */
    public ExpenseDao getExpenseDaoForUser(long chatId) {
        return userDao.getDataType(chatId).equals(DataType.IN_MEMORY)
                ? inMemoryExpenseDao
                : googleSheetsExpenseDao;
    }
}
