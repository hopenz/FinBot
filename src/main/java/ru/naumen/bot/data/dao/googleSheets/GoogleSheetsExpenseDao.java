package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.client.GoogleSheetsClient;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.ExpenseCategory;
import ru.naumen.bot.service.UserService;
import ru.naumen.bot.utils.GoogleSheetsConverter;

import java.io.IOException;
import java.util.List;

/**
 * Управление расходами пользователей, хранящимся в Google Sheets
 */
@Component
public class GoogleSheetsExpenseDao implements ExpenseDao {

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
     * Конструктор класса GoogleSheetsExpenseDao
     *
     * @param googleSheetsClient    экземпляр клиента Google Sheets
     * @param googleSheetsConverter экземпляр конвертера для преобразования данных в формате Google Sheets
     * @param userService           экземпляр сервиса пользователя для получения информации о пользователе
     */
    public GoogleSheetsExpenseDao(GoogleSheetsClient googleSheetsClient, GoogleSheetsConverter googleSheetsConverter,
                                  UserService userService) {
        this.googleSheetsClient = googleSheetsClient;
        this.googleSheetsConverter = googleSheetsConverter;
        this.userService = userService;
    }

    @Override
    public List<Expense> getExpenses(long chatId) throws GoogleSheetsException {
        List<List<Object>> data;
        try {
            data = googleSheetsClient.readData("Расходы!A2:D",
                    userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }

        return googleSheetsConverter.sheetFormatToExpenses(data);
    }

    @Override
    public void addExpense(long chatId, Expense newExpense) throws GoogleSheetsException {
        List<List<Object>> values = googleSheetsConverter.expenseToSheetFormat(newExpense);
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.appendData("Расходы!A2:D", values, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }

    }

    @Override
    public void addExpenses(long chatId, List<Expense> expenses) throws GoogleSheetsException {
        List<List<Object>> values = googleSheetsConverter.expensesToSheetFormat(expenses);
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.appendData("Расходы!A2:D", values, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }

    }

    @Override
    public void removeExpenses(long chatId) throws GoogleSheetsException {
        try {
            googleSheetsClient.clearSheet("Расходы!A2:D", userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }
    }

    @Override
    public void changeLastExpenseCategory(long chatId, ExpenseCategory newCategory) throws GoogleSheetsException {
        List<List<Object>> values = googleSheetsConverter.stringToSheetFormat(newCategory.toString());
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            List<List<Object>> data = googleSheetsClient.
                    readData("Расходы!A1:C", userService.getGoogleSheetId(chatId));

            googleSheetsClient.updateData("Расходы!C" + data.size(), values, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException(e);
        }
    }
}
