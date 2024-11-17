package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.client.GoogleSheetsClient;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.exception.GoogleSheetsException;
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
    public List<Expense> getExpenses(long chatId) {
        List<List<Object>> data;
        try {
            data = googleSheetsClient.readData("Расходы!A2:C",
                    userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException(e.getMessage(), chatId, "Ошибка получения расходов");
        }

        return googleSheetsConverter.sheetFormatToExpenses(data);
    }

    @Override
    public void addExpense(long chatId, Expense newExpense) {
        List<List<Object>> values = googleSheetsConverter.expenseToSheetFormat(newExpense);
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.appendData("Расходы!A2:C", values, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException(e.getMessage(), chatId, "Ошибка добавления расхода");
        }

    }

    @Override
    public void addExpenses(long chatId, List<Expense> expenses) {
        List<List<Object>> values = googleSheetsConverter.expensesToSheetFormat(expenses);
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.appendData("Расходы!A2:C", values, googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException(e.getMessage(), chatId, "Ошибка добавления расходов");
        }

    }

    @Override
    public void removeExpenses(long chatId) {
        try {
            googleSheetsClient.clearSheet("Расходы!A2:C", userService.getGoogleSheetId(chatId));
        } catch (IOException e) {
            throw new GoogleSheetsException(e.getMessage(), chatId, "Ошибка удаления расходов");
        }
    }
}
