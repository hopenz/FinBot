package ru.naumen.bot.service;

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
 * TODO
 */
@Service
public class GoogleSheetsService {
    /**
     * TODO
     */
    private final Sheets sheetsService;

    /**
     * TODO
     */
    public GoogleSheetsService() throws GeneralSecurityException, IOException {
        String credentialsPath = "src/main/resources/credentials.json";
        FileInputStream serviceAccountStream = new FileInputStream(credentialsPath);

        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Google Sheets Integration")
                .build();
    }

    /**
     * НАДО БУДЕТ ИЗМЕНИТЬ! ОБЕРНУТЬ В TRY CATCH!
     * Изменение название листа в гугл-таблице
     *
     * @param sheetId       идентификатор листа
     * @param newSheetTitle новое название
     * @throws IOException ошибка ввода-вывода
     */
    public void updateSheetTitle(int sheetId, String newSheetTitle, String spreadsheetId) {
        UpdateSheetPropertiesRequest updateRequest = new UpdateSheetPropertiesRequest()
                .setProperties(new SheetProperties().setSheetId(sheetId).setTitle(newSheetTitle))
                .setFields("title");
        System.out.println(spreadsheetId);
        Request request = new Request().setUpdateSheetProperties(updateRequest);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(request));
        try {
            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * НАДО БУДЕТ ИЗМЕНИТЬ! ОБЕРНУТЬ В TRY CATCH!
     * Создание нового листа в таблице с названием
     *
     * @param sheetId    идентификатор листа
     * @param sheetTitle название листа
     * @throws IOException ошибка ввода-вывода
     */
    public void createNewSheet(int sheetId, String sheetTitle, String spreadsheetId)  {
        AddSheetRequest addSheetRequest = new AddSheetRequest()
                .setProperties(new SheetProperties().setSheetId(sheetId).setTitle(sheetTitle));

        Request request = new Request().setAddSheet(addSheetRequest);
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(Collections.singletonList(request));

        try {
            sheetsService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * НАДО БУДЕТ ИЗМЕНИТЬ! ОБЕРНУТЬ В TRY CATCH!
     * Изменение названия гугл-таблицы
     *
     * @param newTitle новое название
     * @throws IOException ошибка ввода-вывода
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
     * НАДО БУДЕТ ИЗМЕНИТЬ! ОБЕРНУТЬ В TRY CATCH!
     * Чтение данных из таблицы
     *
     * @param range диапазон ячеек
     * @return Список списков ячеек из таблицы
     * @throws IOException ошибка ввода-вывода
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
     * @param range  диапазон ячеек
     * @param values список списков динных
     * @throws IOException ошибка ввода-вывода
     */
    public void appendData(String range, List<List<Object>> values, String spreadsheetId) throws IOException {
        ValueRange body = new ValueRange().setValues(values);
        try {
            sheetsService.spreadsheets().values()
                    .append(spreadsheetId, range, body)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
        } catch (IOException e) {
            System.err.println("Error appending data: " + e.getMessage());
        }
    }

    /**
     * НАДО БУДЕТ ИЗМЕНИТЬ! ОБЕРНУТЬ В TRY CATCH!
     * Изменение существующих ячеек в таблице
     *
     * @param range  диапазон ячеек
     * @param values список списков данных
     * @throws IOException ошибка ввода-вывода
     */
    public void updateData(String range, List<List<Object>> values, String spreadsheetId) throws IOException {
        ValueRange body = new ValueRange().setValues(values);

        sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }
}

