package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.service.GoogleSheetsService;
import ru.naumen.bot.service.UserService;
import ru.naumen.bot.utils.RecordConverter;

import java.io.IOException;
import java.util.List;

@Component
public class GoogleSheetsExpenseDao implements ExpenseDao {

    private final GoogleSheetsService googleSheetsService;

    private final RecordConverter recordConverter;

    private final UserService userService;

    public GoogleSheetsExpenseDao(GoogleSheetsService googleSheetsService, RecordConverter recordConverter,
                                  UserService userService) {
        this.googleSheetsService = googleSheetsService;
        this.recordConverter = recordConverter;
        this.userService = userService;
    }

    @Override
    public List<Expense> getExpenses(long chatId) {
        return List.of();
    }

    @Override
    public void addExpense(long chatId, Expense newExpense) {
        List<List<Object>> values = recordConverter.expenseToSheetFormat(newExpense);
        String googleSheetLink = userService.getGoogleSheetLink(chatId);
        try {
            googleSheetsService.appendData("Расходы!A:C", values, googleSheetLink);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addExpenses(long chatId, List<Expense> expenses) {
        List<List<Object>> values = recordConverter.expensesToSheetFormat(expenses);
        String googleSheetLink = userService.getGoogleSheetLink(chatId);
        try {
            googleSheetsService.appendData("Расходы!A:C", values, googleSheetLink);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeExpenses(long chatId) {

    }
}
