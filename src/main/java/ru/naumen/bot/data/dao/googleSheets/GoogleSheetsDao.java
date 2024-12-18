package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;
import ru.naumen.bot.client.GoogleSheetsClient;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.exception.DaoException;
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
    public void initGoogleSheets(long chatId) throws DaoException {
        String googleSheetId = userService.getGoogleSheetId(chatId);
        try {
            googleSheetsClient.updateSpreadsheetTitle("FinBot", googleSheetId);
            googleSheetsClient.updateSheetTitle(0, "Общая информация", googleSheetId);
            googleSheetsClient.createNewSheet(1, "Расходы", googleSheetId);
            googleSheetsClient.createNewSheet(2, "Доходы", googleSheetId);
            googleSheetsClient.updateData("Расходы!A1:D1",
                    List.of(List.of("Описание", "Сумма", "Категория", "Дата")), googleSheetId);
            googleSheetsClient.updateData("Доходы!A1:C1",
                    List.of(List.of("Описание", "Сумма", "Дата")), googleSheetId);
            googleSheetsClient.updateData("Общая информация!A1",
                    List.of(List.of("Баланс:")), googleSheetId);
            googleSheetsClient.updateData("Общая информация!A3",
                    List.of(List.of("Лимит на день:")), googleSheetId);
            googleSheetsClient.updateData("Общая информация!B3",
                    List.of(List.of("Сумма расходов за день:")), googleSheetId);
        } catch (IOException e) {
            throw new GoogleSheetsException("Ошибка инициализации таблицы", e);
        }

    }
}
