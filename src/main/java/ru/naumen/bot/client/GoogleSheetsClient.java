package ru.naumen.bot.client;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Клиент для взаимодействия с Google Sheets API
 */
@Service
public class GoogleSheetsClient {

    /**
     * Путь к файлу с учетными данными Google API
     */
    private final static String credentialsPath = "src/main/resources/credentials.json";

    /**
     * Экземпляр сервиса Google Sheets
     */
    private final Sheets sheetsService;

    /**
     * Конструктор, который инициализирует сервис Google Sheets
     */
    public GoogleSheetsClient() throws GeneralSecurityException, IOException {
        try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
            sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Google Sheets Integration")
                    .build();
        }
    }

    /**
     * Изменение название листа в гугл-таблице
     *
     * @param sheetId       идентификатор листа
     * @param newSheetTitle новое название
     */
    public void updateSheetTitle(int sheetId, String newSheetTitle, String spreadsheetId) throws IOException {
        UpdateSheetPropertiesRequest updateRequest = new UpdateSheetPropertiesRequest()
                .setProperties(new SheetProperties().setSheetId(sheetId).setTitle(newSheetTitle))
                .setFields("title");
        Request request = new Request().setUpdateSheetProperties(updateRequest);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(request));

        sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

    }

    /**
     * Создание нового листа в таблице с названием
     *
     * @param sheetId       идентификатор листа
     * @param sheetTitle    название листа
     * @param spreadsheetId идентификатор гугл-таблицы
     */
    public void createNewSheet(int sheetId, String sheetTitle, String spreadsheetId) throws IOException {
        AddSheetRequest addSheetRequest = new AddSheetRequest()
                .setProperties(new SheetProperties().setSheetId(sheetId).setTitle(sheetTitle));

        Request request = new Request().setAddSheet(addSheetRequest);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(request));

        sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
    }

    /**
     * Изменение названия гугл-таблицы
     *
     * @param newTitle      новое название
     * @param spreadsheetId идентификатор гугл-таблицы
     */
    public void updateSpreadsheetTitle(String newTitle, String spreadsheetId) throws IOException {
        UpdateSpreadsheetPropertiesRequest updateRequest = new UpdateSpreadsheetPropertiesRequest()
                .setProperties(new SpreadsheetProperties().setTitle(newTitle))
                .setFields("title");

        Request request = new Request().setUpdateSpreadsheetProperties(updateRequest);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(request));

        sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
    }

    /**
     * Чтение данных из таблицы
     *
     * @param range         диапазон ячеек
     * @param spreadsheetId идентификатор гугл-таблицы
     * @return Список списков ячеек из таблицы
     */
    public List<List<Object>> readData(String range, String spreadsheetId) throws IOException {
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        return response.getValues();
    }

    /**
     * Добавление данных в таблицу
     *
     * @param range         диапазон ячеек
     * @param values        список списков данных
     * @param spreadsheetId идентификатор гугл-таблицы
     */
    public void appendData(String range, List<List<Object>> values, String spreadsheetId) throws IOException {
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();
    }

    /**
     * Изменение существующих ячеек в таблице
     *
     * @param range         диапазон ячеек
     * @param values        список списков данных
     * @param spreadsheetId идентификатор гугл-таблицы
     */
    public void updateData(String range, List<List<Object>> values, String spreadsheetId) throws IOException {
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    /**
     * Очистка листа
     *
     * @param range         диапазон ячеек
     * @param spreadsheetId идентификатор гугл-таблицы
     */
    public void clearSheet(String range, String spreadsheetId) throws IOException {
        ClearValuesRequest clearRequest = new ClearValuesRequest();
        sheetsService.spreadsheets().values().clear(spreadsheetId, range, clearRequest).execute();
    }
}

