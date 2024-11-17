package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.client.GoogleSheetsClient;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.exception.GoogleSheetsException;
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
    public List<Income> getIncomes(long chatId) {
        List<List<Object>> data;
        try {
            data = googleSheetsClient.readData("Доходы!A2:C",
                    userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException(e.getMessage(), chatId, "Ошибка получения доходов");
        }

        return googleSheetsConverter.sheetFormatToIncomes(data);
    }

    @Override
    public void addIncome(long chatId, Income newIncome) {
        List<List<Object>> values = googleSheetsConverter.incomeToSheetFormat(newIncome);
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.appendData("Доходы!A2:C", values, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException(e.getMessage(), chatId, "Ошибка добавления дохода");
        }
    }

    @Override
    public void addIncomes(long chatId, List<Income> incomes) {
        List<List<Object>> values = googleSheetsConverter.incomesToSheetFormat(incomes);
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.appendData("Доходы!A2:C", values, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException(e.getMessage(), chatId, "Ошибка добавления доходов");
        }

    }

    @Override
    public void removeIncomes(long chatId) {
        try {
            googleSheetsClient.clearSheet("Доходы!A2:C", userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException(e.getMessage(), chatId, "Ошибка удаления доходов");
        }
    }
}