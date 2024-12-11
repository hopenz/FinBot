package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.client.GoogleSheetsClient;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.service.UserService;
import ru.naumen.bot.utils.GoogleSheetsConverter;

import java.io.IOException;
import java.util.List;

/**
 * Класс для управления доходами пользователей, хранящимся в Google Sheets
 */
@Component
public class GoogleSheetsIncomeDao implements IncomeDao {

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
     * Конструктор класса GoogleSheetsIncomeDao
     *
     * @param googleSheetsClient    экземпляр клиента Google Sheets
     * @param googleSheetsConverter экземпляр конвертера для преобразования данных
     * @param userService           экземпляр сервиса пользователя
     */
    public GoogleSheetsIncomeDao(GoogleSheetsClient googleSheetsClient,
                                 GoogleSheetsConverter googleSheetsConverter, UserService userService) {
        this.googleSheetsClient = googleSheetsClient;
        this.googleSheetsConverter = googleSheetsConverter;
        this.userService = userService;
    }

    @Override
    public List<Income> getIncomes(long chatId) throws GoogleSheetsException {
        List<List<Object>> data;
        try {
            data = googleSheetsClient.readData("Доходы!A2:C",
                    userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException("Ошибка получения доходов", e);
        }

        return googleSheetsConverter.sheetFormatToIncomes(data);
    }

    @Override
    public void addIncome(long chatId, Income newIncome) throws GoogleSheetsException {
        List<List<Object>> values = googleSheetsConverter.incomeToSheetFormat(newIncome);
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.appendData("Доходы!A2:C", values, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException("Ошибка добавления дохода", e);
        }
    }

    @Override
    public void addIncomes(long chatId, List<Income> incomes) throws GoogleSheetsException {
        List<List<Object>> values = googleSheetsConverter.incomesToSheetFormat(incomes);
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.appendData("Доходы!A2:C", values, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException("Ошибка добавления доходов", e);
        }

    }

    @Override
    public void removeIncomes(long chatId) throws GoogleSheetsException {
        try {
            googleSheetsClient.clearSheet("Доходы!A2:C", userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException("Ошибка удаления доходов", e);
        }
    }
}
