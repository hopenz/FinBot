package ru.naumen.bot.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.exception.GoogleSheetsException;
import ru.naumen.bot.processor.exception.handler.GoogleSheetsExceptionHandler;
import ru.naumen.bot.service.DatabaseService;
import ru.naumen.bot.service.UserService;

/**
 * Тесты для класса {@link CallbackQueryProcessor}, который обрабатывает callback-запросы
 * от inline-кнопок в чате с пользователем.
 */
public class CallbackQueryProcessorTest {

    /**
     * Мок-объект для {@link BotController}, используемый для отправки сообщений пользователям.
     */
    private BotController botController;

    /**
     * Мок-объект для {@link UserService}, используемый для проверки статуса чата.
     */
    private UserService userServiceMock;

    /**
     * Мок-объект для {@link DatabaseService}, используемый для работы с базами данных.
     */
    private DatabaseService databaseServiceMock;

    /**
     * Мок-объект для {@link GoogleSheetsExceptionHandler}, используемый для обработки исключений Google Sheets.
     */
    private GoogleSheetsExceptionHandler exceptionHandler;

    /**
     * Тестируемый объект {@link CallbackQueryProcessor}, который проверяется в данном тестовом классе.
     */
    private CallbackQueryProcessor queryProcessor;

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345L;

    /**
     * Инициализация всех зависимостей и {@link CallbackQueryProcessor} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        botController = Mockito.mock(BotController.class);
        userServiceMock = Mockito.mock(UserService.class);
        databaseServiceMock = Mockito.mock(DatabaseService.class);
        exceptionHandler = Mockito.mock(GoogleSheetsExceptionHandler.class);

        queryProcessor = new CallbackQueryProcessor(botController, userServiceMock, databaseServiceMock, exceptionHandler);
    }

    /**
     * Тест для метода {@link CallbackQueryProcessor#processCallbackQuery(String, Long, String)}.
     * Проверяет:
     * <ul>
     *     <li>Правильность обработки состояния {@code WAITING_FOR_TYPE_DB}.</li>
     *     <li>Вызов соответствующего обработчика для выбора типа базы данных.</li>
     * </ul>
     */
    @Test
    void testProcessCallbackQuery_WaitingForTypeDB() {
        String data = "Гугл-таблица";
        String queryId = "queryId";

        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_TYPE_DB);

        queryProcessor.processCallbackQuery(data, chatId, queryId);

        Mockito.verify(botController).sendPopUpMessage("Вы выбрали " + data, queryId);
        Mockito.verify(userServiceMock).getUserState(chatId);
        Mockito.verifyNoInteractions(databaseServiceMock);
    }

    /**
     * Тест для метода {@link CallbackQueryProcessor#processCallbackQuery(String, Long, String)}.
     * Проверяет обработку состояния {@code WAITING_FOR_TYPE_DB_FOR_CHANGE_DB}.
     */
    @Test
    void testProcessCallbackQuery_WaitingForTypeDBForChangeDB() {
        String data = "Гугл-таблица";
        String queryId = "queryId";

        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_TYPE_DB_FOR_CHANGE_DB);
        Mockito.when(userServiceMock.getDataType(chatId)).thenReturn(DataType.IN_MEMORY);
        Mockito.when(userServiceMock.getGoogleSheetId(chatId)).thenReturn(null);

        queryProcessor.processCallbackQuery(data, chatId, queryId);

        Mockito.verify(userServiceMock).getDataType(chatId);
        Mockito.verify(userServiceMock).getGoogleSheetId(chatId);
        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);
        Mockito.verify(botController).sendMessage(Mockito.contains("""
                Давайте создадим таблицу и привяжем к ней бота:
                1. Для создания таблицы перейдите по ссылке https://sheets.new/
                2. Добавьте finbotaccount@celtic-house-440906-m1.iam.gserviceaccount.com в качестве пользователя-редактор
                3. Пришлите мне ссылку на новую созданную таблицу (просто скопируйте и отправьте мне), остальное я сделаю сам
                """), Mockito.eq(chatId));
    }

    /**
     * Тест для метода {@link CallbackQueryProcessor#processCallbackQuery(String, Long, String)}.
     * Проверяет обработку состояния, если выбран тип базы данных, который уже используется.
     */
    @Test
    void testProcessCallbackQuery_AlreadyUsingSameDB() {
        String data = "Гугл-таблица";
        String queryId = "queryId";

        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_TYPE_DB_FOR_CHANGE_DB);
        Mockito.when(userServiceMock.getDataType(chatId)).thenReturn(DataType.IN_GOOGLE_SHEET);

        queryProcessor.processCallbackQuery(data, chatId, queryId);

        Mockito.verify(userServiceMock).getDataType(chatId);
        Mockito.verify(botController).sendMessage("Вы уже используете этот способ хранения", chatId);
    }

    /**
     * Тест для метода {@link CallbackQueryProcessor#processCallbackQuery(String, Long, String)}.
     * Проверяет корректную смену базы данных.
     */
    @Test
    void testProcessCallbackQuery_ChangeDBSuccessfully() {
        String data = "Гугл-таблица";
        String queryId = "queryId";

        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_TYPE_DB_FOR_CHANGE_DB);
        Mockito.when(userServiceMock.getDataType(chatId)).thenReturn(DataType.IN_MEMORY);
        Mockito.when(userServiceMock.getGoogleSheetId(chatId)).thenReturn("https://example.com");

        queryProcessor.processCallbackQuery(data, chatId, queryId);

        Mockito.verify(databaseServiceMock).changeDB(chatId, DataType.IN_GOOGLE_SHEET);
        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
    }

    /**
     * Тест для метода {@link CallbackQueryProcessor#processCallbackQuery(String, Long, String)}.
     * Проверяет корректную обработку GoogleSheetsException.
     */
    @Test
    void testProcessCallbackQuery_ChangeDBWithGoogleSheetsException() {
        String data = "Гугл-таблица";
        String queryId = "queryId";

        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.WAITING_FOR_TYPE_DB_FOR_CHANGE_DB);
        Mockito.when(userServiceMock.getDataType(chatId)).thenReturn(DataType.IN_MEMORY);
        Mockito.when(userServiceMock.getGoogleSheetId(chatId)).thenReturn("https://example.com");
        GoogleSheetsException exception = new GoogleSheetsException(new Exception());
        Mockito.doThrow(exception).when(databaseServiceMock).changeDB(chatId, DataType.IN_GOOGLE_SHEET);

        queryProcessor.processCallbackQuery(data, chatId, queryId);

        Mockito.verify(botController).sendMessage("Во время смены базы данных произошла ошибка", chatId);
        Mockito.verify(exceptionHandler).handleGoogleSheetsException(exception, chatId);
    }
}
