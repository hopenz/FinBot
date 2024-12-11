package ru.naumen.bot.data.dao.provider;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.dao.googleSheets.GoogleSheetsBalanceDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryBalanceDao;
import ru.naumen.bot.data.enums.DataType;

/**
 * Класс предоставляет доступ к DAO-объектам для работы с балансом пользователя.
 */
@Component
public class BalanceDaoProvider {

    /**
     * Экземпляр DAO, использующий Google Sheets для хранения баланса.
     */
    private final GoogleSheetsBalanceDao googleSheetsBalanceDao;

    /**
     * Экземпляр DAO, использующий память для хранения баланса.
     */
    private final InMemoryBalanceDao inMemoryBalanceDao;

    /**
     * Экземпляр DAO для получения информации о пользователе.
     */
    private final UserDao userDao;

    /**
     * Конструктор класса {@link BalanceDaoProvider}.
     *
     * @param googleSheetsBalanceDao Экземпляр DAO для работы с Google Sheets.
     * @param inMemoryBalanceDao     Экземпляр DAO для работы с памятью.
     * @param userDao                Экземпляр DAO для получения информации о пользователе.
     */
    public BalanceDaoProvider(GoogleSheetsBalanceDao googleSheetsBalanceDao,
                              InMemoryBalanceDao inMemoryBalanceDao, UserDao userDao) {
        this.googleSheetsBalanceDao = googleSheetsBalanceDao;
        this.inMemoryBalanceDao = inMemoryBalanceDao;
        this.userDao = userDao;
    }

    /**
     * Возвращает DAO для работы с балансом пользователя для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить DAO
     * @return InMemoryBalanceDao, если DataType пользователя равен IN_MEMORY, иначе GoogleSheetsBalanceDao.
     */
    public BalanceDao getBalanceDaoForUser(long chatId) {
        return userDao.getDataType(chatId).equals(DataType.IN_MEMORY)
                ? inMemoryBalanceDao
                : googleSheetsBalanceDao;

    }
}
