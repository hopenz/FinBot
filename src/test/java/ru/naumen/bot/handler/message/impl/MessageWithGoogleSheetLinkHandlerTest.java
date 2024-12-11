package ru.naumen.bot.handler.message.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.data.enums.DataType;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.service.DatabaseService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Тесты для класса {@link MessageWithGoogleSheetLinkHandler}, проверяющие обработку сообщений со ссылкой на Google Sheet.
 */
public class MessageWithGoogleSheetLinkHandlerTest {

    /**
     * Мок-объект для {@link DatabaseService}, используемый для управления источником данных.
     */
    private final DatabaseService databaseServiceMock = Mockito.mock(DatabaseService.class);

    /**
     * Мок-объект для {@link UserService}, используемый для работы с данными пользователей.
     */
    private final UserService userServiceMock = Mockito.mock(UserService.class);

    /**
     * Тестируемый объект {@link MessageWithGoogleSheetLinkHandler}, который обрабатывает сообщения со ссылками.
     */
    private final MessageWithGoogleSheetLinkHandler messageWithGoogleSheetLinkHandler
            = new MessageWithGoogleSheetLinkHandler(databaseServiceMock, userServiceMock);

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345L;

    /**
     * Тест для проверки обработки сообщения с корректной ссылкой на Google Sheet.
     * Проверяет, что ссылка сохраняется, данные источника меняются, и статус чата изменяется.
     */
    @Test
    void testHandleMessageWithValidLink() throws DaoException {
        String message = "https://docs.google.com/spreadsheets/" +
                "d/1N-8SSwwV28lk7q6iJvrrlezmzZTpwuR-n00ny1HgA8o/edit?gid=0#gid=0";
        List<AnswerMessage> expected = List.of(new AnswerMessage("Бот готов к работе!", chatId));
        List<AnswerMessage> response =
                messageWithGoogleSheetLinkHandler.handleMessage(message, chatId);

        Mockito.verify(userServiceMock).setGoogleSheetId(chatId, message);
        Mockito.verify(databaseServiceMock).changeDB(chatId, DataType.IN_GOOGLE_SHEET);
        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тест для проверки обработки сообщения с некорректной ссылкой на Google Sheet.
     * Проверяет, что ссылка не сохраняется, источник данных не изменяется, а статус чата изменяется.
     */
    @Test
    void testHandleMessageWithInvalidLink() throws DaoException {
        String message = "https://docs.goooogle.com/spreadsheets/" +
                "d/1N-8SSwwV28lk7q6iJvrrlezmzZTpwuR-n00ny1HgA8o/edit?gid=0#gid=0";
        List<AnswerMessage> expected = List.of(
                new AnswerMessage("Ссылка на Google Sheet некорректна", chatId));
        List<AnswerMessage> response =
                messageWithGoogleSheetLinkHandler.handleMessage(message, chatId);

        Mockito.verifyNoInteractions(databaseServiceMock);
        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Mockito.verifyNoMoreInteractions(userServiceMock);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}