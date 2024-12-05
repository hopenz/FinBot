package ru.naumen.bot.handler.callback.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Тесты для обработчика коллбэков {@link TypeDBForStartCallbackHandler}, который обрабатывает выбор типа базы данных
 * для пользователя при старте работы с ботом.
 */
public class TypeDBForStartCallbackHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для управления состоянием пользователя.
     */
    private UserService userServiceMock;

    /**
     * Тестируемый обработчик коллбэков {@link TypeDBForStartCallbackHandler}.
     */
    private TypeDBForStartCallbackHandler callbackHandler;

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
    public void setUp() {
        userServiceMock = Mockito.mock(UserService.class);
        callbackHandler = new TypeDBForStartCallbackHandler(userServiceMock);
    }

    /**
     * Тест обработки коллбэка с выбором типа базы данных Google Sheets.
     * Проверяет, что бот корректно отреагирует на выбор Google Sheets и отправит правильные сообщения.
     */
    @Test
    void testHandleCallbackWithGoogleSheetsType() {
        List<AnswerMessage> expected =
                List.of(
                        new AnswerMessage("Помните, вы можете поменять способ хранения данных" +
                                " с помощью команды " + Commands.CHANGE_DB_COMMAND.getCommand(), chatId),
                        new AnswerMessage("""
                                Давайте создадим таблицу и привяжем к ней бота:
                                1. Для создания таблицы перейдите по ссылке https://sheets.new/
                                2. Добавьте finbotaccount@celtic-house-440906-m1.iam.gserviceaccount.com в качестве пользователя-редактор
                                3. Пришлите мне ссылку на новую созданную таблицу (просто скопируйте и отправьте мне), остальное я сделаю сам
                                """, chatId));
        List<AnswerMessage> response =
                callbackHandler.handleCallback(TypeDBKeyboard.GOOGLE_SHEETS.getData(), callbackId, chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(2);
    }

    /**
     * Тест обработки коллбэка с выбором типа базы данных In-Memory.
     * Проверяет, что бот корректно отреагирует на выбор In-Memory и отправит правильные сообщения.
     */
    @Test
    void testHandleCallbackWithInMemoryType() {
        List<AnswerMessage> expected =
                List.of(
                        new AnswerMessage("Помните, вы можете поменять способ хранения данных" +
                                " с помощью команды " + Commands.CHANGE_DB_COMMAND.getCommand(), chatId),
                        new AnswerMessage("Теперь ваши данные хранятся в оперативной памяти!", chatId));
        List<AnswerMessage> response =
                callbackHandler.handleCallback(TypeDBKeyboard.IN_MEMORY.getData(), callbackId, chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(2);
    }
}