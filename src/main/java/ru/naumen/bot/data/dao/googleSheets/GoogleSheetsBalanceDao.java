package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.client.GoogleSheetsClient;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.service.UserService;
import ru.naumen.bot.utils.GoogleSheetsConverter;

import java.io.IOException;
import java.util.List;

/**
 * Управление балансом пользователей, хранящимся в Google Sheets
 */
@Component
public class GoogleSheetsBalanceDao implements BalanceDao {

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
     * Конструктор класса GoogleSheetsBalanceDao
     *
     * @param googleSheetsClient    Экземпляр клиента Google Sheets
     * @param googleSheetsConverter Конвертер для преобразования данных в формате Google Sheets
     * @param userService           Экземпляр сервиса пользователя для получения информации о пользователе
     */
    public GoogleSheetsBalanceDao(GoogleSheetsClient googleSheetsClient, GoogleSheetsConverter googleSheetsConverter,
                                  UserService userService) {
        this.googleSheetsClient = googleSheetsClient;
        this.googleSheetsConverter = googleSheetsConverter;
        this.userService = userService;
    }

    @Override
    public void setBalance(long chatId, double newBalance) throws GoogleSheetsException {
        List<List<Object>> value = googleSheetsConverter.doubleToSheetFormat(newBalance);
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.updateData("Общая информация!B1", value, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }

    }

    @Override
    public Double getBalance(long chatId) throws GoogleSheetsException {
        List<List<Object>> data;
        try {
            data = googleSheetsClient.readData("Общая информация!B1",
                    userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }
        return googleSheetsConverter.sheetFormatToDouble(data);
    }

    @Override
    public void removeBalance(long chatId) throws GoogleSheetsException {
        try {
            googleSheetsClient.clearSheet("Общая информация!B1", userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }
    }
}
