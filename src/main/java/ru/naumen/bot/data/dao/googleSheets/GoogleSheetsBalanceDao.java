package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.service.GoogleSheetsService;
import ru.naumen.bot.service.UserService;
import ru.naumen.bot.utils.RecordConverter;

@Component
public class GoogleSheetsBalanceDao implements BalanceDao {

    private final GoogleSheetsService googleSheetsService;

    private final RecordConverter recordConverter;

    private final UserService userService;

    public GoogleSheetsBalanceDao(GoogleSheetsService googleSheetsService, RecordConverter recordConverter, UserService userService) {
        this.googleSheetsService = googleSheetsService;
        this.recordConverter = recordConverter;
        this.userService = userService;
    }

    @Override
    public void setBalance(long chatId, double newBalance) {

    }

    @Override
    public Double getBalance(long chatId) {
        return 0.0;
    }

    @Override
    public void removeBalance(long chatId) {

    }
}
