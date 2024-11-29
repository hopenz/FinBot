package ru.naumen.bot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.googleSheets.GoogleSheetsDao;
import ru.naumen.bot.exception.DaoException;

/**
 * Сервис для взаимодействия с Google Sheets.
 */
@Service
public class GoogleSheetsService {

    /**
     * Dao для работы с гугл таблицами
     */
    private final GoogleSheetsDao googleSheetsDao;

    /**
     * Конструктор класса GoogleSheetsService
     *
     * @param googleSheetsDao dao для работы с гугл таблицами
     */
    public GoogleSheetsService(GoogleSheetsDao googleSheetsDao) {
        this.googleSheetsDao = googleSheetsDao;
    }

    /**
     * Инициализация гугл-таблицы для указанного чата
     *
     * @param chatId идентификатор чата, для которого инициализируется таблица
     */
    public void initGoogleSheets(long chatId) throws DaoException {
        googleSheetsDao.initGoogleSheets(chatId);
    }
}
