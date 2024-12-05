package ru.naumen.bot.handler.callback.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.service.DatabaseService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Тесты для обработчика коллбэков {@link TypeDBForChangeCallbackHandler}, который обрабатывает выбор типа базы данных
 * при изменении типа хранения данных пользователем.
 */
public class TypeDBForChangeCallbackHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для управления состоянием пользователя и данных.
     */
    private UserService userServiceMock;

    /**
     * Мок-объект для {@link DatabaseService}, используемый для изменения типа базы данных.
     */
    private DatabaseService databaseServiceMock;

    /**
     * Тестируемый обработчик коллбэков {@link TypeDBForChangeCallbackHandler}.
     */
    private TypeDBForChangeCallbackHandler callbackHandler;

    /**
     * Идентификатор чата пользователя.
     */
    private final long chatId = 12345L;

    /**
     * Идентификатор коллбэка.
     */
    private final String callbackId = "id";

    /**
     * Инициализация зависимостей перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userServiceMock = Mockito.mock(UserService.class);
        databaseServiceMock = Mockito.mock(DatabaseService.class);

        callbackHandler = new TypeDBForChangeCallbackHandler(databaseServiceMock, userServiceMock);
    }

    /**
     * Тест обработки коллбэка при попытке выбрать тот же тип базы данных, который уже используется.
     * Проверяет, что бот отправляет сообщение о том, что выбранный тип базы данных уже используется.
     */
    @Test
    void testHandleCallbackWithSameDBType() throws DaoException {
        Mockito.when(userServiceMock.getDataType(chatId)).thenReturn(DataType.IN_GOOGLE_SHEET);
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("Вы уже используете этот способ хранения", chatId));
        List<AnswerMessage> response =
                callbackHandler.handleCallback(TypeDBKeyboard.GOOGLE_SHEETS.getData(), callbackId, chatId);

        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тест обработки коллбэка с выбором Google Sheets, когда текущий тип базы данных - In-Memory.
     * Проверяет, что бот отправляет инструкцию по созданию таблицы Google Sheets, если таблица еще не существует.
     */
    @Test
    void testHandleCallbackWithoutGoogleSheetId() throws DaoException {
        Mockito.when(userServiceMock.getDataType(chatId)).thenReturn(DataType.IN_MEMORY);
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("""
                        Давайте создадим таблицу и привяжем к ней бота:
                        1. Для создания таблицы перейдите по ссылке https://sheets.new/
                        2. Добавьте finbotaccount@celtic-house-440906-m1.iam.gserviceaccount.com в качестве пользователя-редактор
                        3. Пришлите мне ссылку на новую созданную таблицу (просто скопируйте и отправьте мне), остальное я сделаю сам
                        """, chatId));
        List<AnswerMessage> response =
                callbackHandler.handleCallback(TypeDBKeyboard.GOOGLE_SHEETS.getData(), callbackId, chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тест обработки коллбэка с выбором Google Sheets, когда уже есть Google Sheets ID.
     * Проверяет, что бот меняет тип базы данных и отправляет сообщение об успешном изменении.
     */
    @Test
    void testHandleCallbackWithChangeDB() throws DaoException {
        Mockito.when(userServiceMock.getDataType(chatId)).thenReturn(DataType.IN_MEMORY);
        Mockito.when(userServiceMock.getGoogleSheetId(chatId)).thenReturn(Mockito.anyString());
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("Теперь ваши данные хранятся в '" +
                        TypeDBKeyboard.GOOGLE_SHEETS.getData() + "'!", chatId));
        List<AnswerMessage> response =
                callbackHandler.handleCallback(TypeDBKeyboard.GOOGLE_SHEETS.getData(), callbackId, chatId);

        Mockito.verify(databaseServiceMock).changeDB(chatId, DataType.IN_GOOGLE_SHEET);
        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}