package ru.naumen.bot.data.dao.provider;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.LimitDao;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.dao.googleSheets.GoogleSheetsLimitDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryLimitDao;
import ru.naumen.bot.data.entity.DataType;

/**
 * Класс предоставляет доступ к DAO-объектам для работы с лимитами пользователя.
 */
@Component
public class LimitDaoProvider {

    /**
     * Экземпляр DAO, использующий Google Sheets для хранения лимита.
     */
    private final GoogleSheetsLimitDao googleSheetsLimitDao;

    /**
     * Экземпляр DAO, использующий память для хранения лимита.
     */
    private final InMemoryLimitDao inMemoryLimitDao;

    /**
     * Экземпляр DAO для получения информации о пользователе.
     */
    private final UserDao userDao;

    /**
     * Конструктор класса {@link LimitDaoProvider}.
     *
     * @param googleSheetsLimitDao Экземпляр DAO для работы с Google Sheets.
     * @param inMemoryLimitDao     Экземпляр DAO для работы с памятью.
     * @param userDao              Экземпляр DAO для получения информации о пользователе.
     */
    public LimitDaoProvider(GoogleSheetsLimitDao googleSheetsLimitDao,
                            InMemoryLimitDao inMemoryLimitDao, UserDao userDao) {
        this.googleSheetsLimitDao = googleSheetsLimitDao;
        this.inMemoryLimitDao = inMemoryLimitDao;
        this.userDao = userDao;
    }

    /**
     * Возвращает DAO для работы с лимитом пользователя для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить DAO
     * @return InMemoryLimitDao, если DataType пользователя равен IN_MEMORY, иначе GoogleSheetsLimitDao.
     */
    public LimitDao getLimitDaoForUser(long chatId) {
        return userDao.getDataType(chatId).equals(DataType.IN_MEMORY)
                ? inMemoryLimitDao
                : googleSheetsLimitDao;
    }
}
