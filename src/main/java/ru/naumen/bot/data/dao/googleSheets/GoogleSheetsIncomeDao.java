package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.service.GoogleSheetsService;
import ru.naumen.bot.service.UserService;
import ru.naumen.bot.utils.RecordConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO
 */
@Component
public class GoogleSheetsIncomeDao implements IncomeDao {

    private final GoogleSheetsService googleSheetsService;

    private final RecordConverter recordConverter;

    private final UserService userService;

    public GoogleSheetsIncomeDao(GoogleSheetsService googleSheetsService, RecordConverter recordConverter, UserService userService) {
        this.googleSheetsService = googleSheetsService;
        this.recordConverter = recordConverter;
        this.userService = userService;
    }

    @Override
    public List<Income> getIncomes(long chatId) {
        return List.of();
    }

    @Override
    public void addIncome(long chatId, Income newIncome) {
        List<List<Object>> values = recordConverter.incomeToSheetFormat(newIncome);
        String googleSheetLink = userService.getGoogleSheetLink(chatId);
        try {
            googleSheetsService.appendData("Доходы!A:C", values, googleSheetLink);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addIncomes(long chatId, List<Income> incomes) {
        List<List<Object>> values = recordConverter.incomesToSheetFormat(incomes);
        String googleSheetLink = userService.getGoogleSheetLink(chatId);
        try {
            googleSheetsService.appendData("Доходы!A:C", values, googleSheetLink);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeIncomes(long chatId) {

    }
}
