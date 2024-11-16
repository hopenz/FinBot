package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.service.GoogleSheetsService;
import ru.naumen.bot.service.UserService;

@Component
public class GoogleSheetsDao {

    private final GoogleSheetsService googleSheetsService;

    private final UserService userService;

    public GoogleSheetsDao(GoogleSheetsService googleSheetsService, UserService userService) {
        this.googleSheetsService = googleSheetsService;
        this.userService = userService;
    }

    public void initGoogleSheets(long chatId) {
        String googleSheetId = userService.getGoogleSheetLink(chatId);
        googleSheetsService.updateSheetTitle(0, "Общая информация", googleSheetId);
        googleSheetsService.createNewSheet(1, "Расходы", googleSheetId);
        googleSheetsService.createNewSheet(2, "Доходы", googleSheetId);
    }
}
