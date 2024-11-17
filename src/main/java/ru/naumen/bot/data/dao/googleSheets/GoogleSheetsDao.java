package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.client.GoogleSheetsClient;
import ru.naumen.bot.exception.GoogleSheetsException;
import ru.naumen.bot.service.UserService;

import java.io.IOException;
import java.util.List;

/**
 * Класс для работы с гугл таблицами
 */
@Component
public class GoogleSheetsDao {

    /**
     * Экземпляр клиента Google Sheets
     */
    private final GoogleSheetsClient googleSheetsClient;

    /**
     * Экземпляр сервиса пользователя для получения информации о пользователе
     */
    private final UserService userService;

    /**
     * Конструктор класса GoogleSheetsDao
     *
     * @param googleSheetsClient Экземпляр клиента Google Sheets
     * @param userService        Экземпляр сервиса пользователя для получения информации о пользователе
     */
    public GoogleSheetsDao(GoogleSheetsClient googleSheetsClient, UserService userService) {
        this.googleSheetsClient = googleSheetsClient;
        this.userService = userService;
    }

    /**
     * Инициализация гугл-таблицы для указанного чата
     * Создание листов, заполнение начальными данными
     *
     * @param chatId идентификатор чата, для которого инициализируется таблица
     */
    public void initGoogleSheets(long chatId) {
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.updateSpreadsheetTitle("FinBot", googleSheetId);
            googleSheetsClient.updateSheetTitle(0, "Общая информация", googleSheetId);
            googleSheetsClient.createNewSheet(1, "Расходы", googleSheetId);
            googleSheetsClient.createNewSheet(2, "Доходы", googleSheetId);
            googleSheetsClient.updateData("Расходы!A1:C1", List.of(List.of("Описание", "Сумма", "Дата")),
                    googleSheetId);
            googleSheetsClient.updateData("Доходы!A1:C1", List.of(List.of("Описание", "Сумма", "Дата")),
                    googleSheetId);
            googleSheetsClient.updateData("Общая информация!A1", List.of(List.of("Баланс:")), googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException(e.getMessage(), chatId, "Ошибка инициализации таблицы");
        }

    }
}
