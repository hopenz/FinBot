package ru.naumen.bot.data.dao.provider;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.dao.googleSheets.GoogleSheetsIncomeDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryIncomeDao;
import ru.naumen.bot.data.entity.DataType;

/**
 * Класс предоставляет доступ к DAO-объектам для работы с доходами пользователя.
 */
@Component
public class IncomeDaoProvider {

    /**
     * Экземпляр DAO, использующий Google Sheets для хранения доходов.
     */
    private final GoogleSheetsIncomeDao googleSheetsIncomeDao;

    /**
     * Экземпляр DAO, использующий память для хранения доходов.
     */
    private final InMemoryIncomeDao inMemoryIncomeDao;

    /**
     * Экземпляр DAO для получения информации о пользователе.
     */
    private final UserDao userDao;

    /**
     * Конструктор класса {@link IncomeDaoProvider}.
     *
     * @param googleSheetsIncomeDao Экземпляр DAO для работы с Google Sheets.
     * @param inMemoryIncomeDao     Экземпляр DAO для работы с памятью.
     * @param userDao               Экземпляр DAO для получения информации о пользователе.
     */
    public IncomeDaoProvider(GoogleSheetsIncomeDao googleSheetsIncomeDao,
                             InMemoryIncomeDao inMemoryIncomeDao, UserDao userDao) {
        this.googleSheetsIncomeDao = googleSheetsIncomeDao;
        this.inMemoryIncomeDao = inMemoryIncomeDao;
        this.userDao = userDao;
    }

    /**
     * Возвращает DAO для работы с доходами пользователя для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить DAO
     * @return InMemoryIncomeDao, если DataType пользователя равен IN_MEMORY, иначе GoogleSheetsIncomeDao.
     */
    public IncomeDao getIncomeDaoForUser(long chatId) {
        return userDao.getDataType(chatId).equals(DataType.IN_MEMORY)
                ? inMemoryIncomeDao
                : googleSheetsIncomeDao;
    }
}
