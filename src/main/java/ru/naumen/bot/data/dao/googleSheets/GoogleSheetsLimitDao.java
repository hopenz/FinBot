package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.client.GoogleSheetsClient;
import ru.naumen.bot.data.dao.LimitDao;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.data.entity.Limit;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.service.UserService;
import ru.naumen.bot.utils.GoogleSheetsConverter;

import java.io.IOException;
import java.util.List;

/**
 * Класс для управления лимитами пользователей, хранящимся в Google Sheets
 */
@Component
public class GoogleSheetsLimitDao implements LimitDao {

    /**
     * Экземпляр клиента Google Sheets
     */
    private final GoogleSheetsClient googleSheetsClient;

    /**
     * Конвертер для преобразования данных в формате Google Sheets
     */
    private final GoogleSheetsConverter googleSheetsConverter;

    /**
     * Экземпляр сервиса пользователя для получения информации о пользователе
     */
    private final UserService userService;

    /**
     * Конструктор класса GoogleSheetsLimitDao
     *
     * @param googleSheetsClient    Экземпляр клиента Google Sheets
     * @param googleSheetsConverter Конвертер для преобразования данных в формате Google Sheets
     * @param userService           Экземпляр сервиса пользователя для получения информации о пользователе
     */
    public GoogleSheetsLimitDao(GoogleSheetsClient googleSheetsClient, GoogleSheetsConverter googleSheetsConverter,
                                UserService userService) {
        this.googleSheetsClient = googleSheetsClient;
        this.googleSheetsConverter = googleSheetsConverter;
        this.userService = userService;
    }


    @Override
    public void setLimit(long chatId, Limit limit) throws DaoException {
        List<List<Object>> value = googleSheetsConverter.limitToSheetFormat(limit);
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.updateData("Общая информация!A4:B4", value, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }
    }

    @Override
    public void removeLimit(long chatId) throws DaoException {
        try {
            googleSheetsClient.clearSheet("Общая информация!A4:B4", userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }
    }

    @Override
    public Limit getLimit(long chatId) throws DaoException {
        List<List<Object>> data;
        try {
            data = googleSheetsClient.readData("Общая информация!A4:B4",
                    userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }
        return googleSheetsConverter.sheetFormatToLimit(data);
    }
}
