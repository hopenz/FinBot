package ru.naumen.bot.processor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.BotUpdate;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.data.enums.DataType;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.callback.CallbackHandler;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.handler.message.MessageHandler;
import ru.naumen.bot.interaction.CommandData;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Тесты для класса {@link BotUpdateProcessor}, проверяющие обработку различных обновлений бота.
 */

public class BotUpdateProcessorTest {

    /**
     * Мок-объект для {@link UserService}, используемый для работы с данными пользователей.
     */
    private final UserService userServiceMock = Mockito.mock(UserService.class);

    /**
     * Мок-объект для {@link CommandHandler}, используемый для обработки команд бота.
     */
    private final CommandHandler commandHandlerMock = Mockito.mock(CommandHandler.class);

    /**
     * Мок-объект для {@link CallbackHandler}, используемый для обработки коллбэков.
     */
    private final CallbackHandler callbackHandlerMock = Mockito.mock(CallbackHandler.class);

    /**
     * Мок-объект для {@link MessageHandler}, используемый для обработки сообщений.
     */
    private final MessageHandler messageHandlerMock = Mockito.mock(MessageHandler.class);

    /**
     * Тестируемый объект {@link BotUpdateProcessor}, который обрабатывает обновления бота.
     */
    private BotUpdateProcessor botUpdateProcessor;

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345;

    /**
     * Инициализация всех зависимостей и {@link BotUpdateProcessor} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        Mockito.when(commandHandlerMock.getCommand()).thenReturn(CommandData.START_COMMAND.getReadableName());
        Mockito.when(callbackHandlerMock.getChatState()).thenReturn(ChatState.NOTHING_WAITING);
        Mockito.when(messageHandlerMock.getChatState()).thenReturn(ChatState.NOTHING_WAITING);
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        botUpdateProcessor = new BotUpdateProcessor(
                userServiceMock,
                List.of(commandHandlerMock),
                List.of(callbackHandlerMock),
                List.of(messageHandlerMock)
        );
    }

    /**
     * Тест для проверки обработки обновления с командой, которая не является стартовой,
     * и при этом чат не открыт. Ожидается ответ с просьбой начать работу с ботом.
     */
    @Test
    void testProcessBotUpdate_WithNotStartCommandAndChatNotOpened() {
        BotUpdate botUpdate =
                new BotUpdate(chatId, "/some", null, null);
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(false);

        List<AnswerMessage> response = botUpdateProcessor.processBotUpdate(botUpdate);
        List<AnswerMessage> expected = List.of(new AnswerMessage(
                "Чтобы начать работу, нажмите " + CommandData.START_COMMAND.getReadableName(), chatId));

        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тест для проверки обработки обновления с сообщением, которое обрабатывается {@link MessageHandler}.
     * Ожидается, что сообщение будет обработано, и вернется ответ от {@link MessageHandler}.
     */
    @Test
    void testProcessBotUpdate_WithMessage() throws DaoException {
        BotUpdate botUpdate =
                new BotUpdate(chatId, "some", null, null);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.NOTHING_WAITING);
        Mockito.when(messageHandlerMock.handleMessage("some", chatId)).
                thenReturn(List.of(new AnswerMessage("response", chatId)));

        List<AnswerMessage> response = botUpdateProcessor.processBotUpdate(botUpdate);
        List<AnswerMessage> expected = List.of(new AnswerMessage("response", chatId));

        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тест для проверки обработки обновления с командой, которая обрабатывается {@link CommandHandler}.
     * Ожидается, что команда будет обработана, и вернется ответ от {@link CommandHandler}.
     */
    @Test
    void testProcessBotUpdate_WithCommand() throws DaoException {
        BotUpdate botUpdate =
                new BotUpdate(chatId, CommandData.START_COMMAND.getReadableName(), null, null);
        Mockito.when(commandHandlerMock.handleCommand(CommandData.START_COMMAND.getReadableName(), chatId)).
                thenReturn(List.of(new AnswerMessage("Start response", chatId)));

        List<AnswerMessage> response = botUpdateProcessor.processBotUpdate(botUpdate);
        List<AnswerMessage> expected = List.of(new AnswerMessage("Start response", chatId));

        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тест для проверки обработки обновления с коллбэком, который обрабатывается {@link CallbackHandler}.
     * Ожидается, что коллбэк будет обработан, и вернется ответ от {@link CallbackHandler}.
     */
    @Test
    void testProcessBotUpdate_WithCallback() throws DaoException {
        String callbackData = "data";
        String callbackId = "id";
        BotUpdate botUpdate =
                new BotUpdate(chatId, null, callbackData, callbackId);
        Mockito.when(userServiceMock.getUserState(chatId)).thenReturn(ChatState.NOTHING_WAITING);
        Mockito.when(callbackHandlerMock.handleCallback(callbackData, callbackId, chatId)).
                thenReturn(List.of(new AnswerMessage("Callback response", chatId)));

        List<AnswerMessage> response = botUpdateProcessor.processBotUpdate(botUpdate);
        List<AnswerMessage> expected = List.of(
                new AnswerMessage("Вы выбрали '" + callbackData + "'", callbackId),
                new AnswerMessage("Callback response", chatId));

        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(2);
    }

    /**
     * Тест для проверки обработки обновления, когда происходит ошибка с {@link GoogleSheetsException}.
     * Ожидается, что будет возвращено сообщение об ошибке и бот перейдет в режим работы с данными в памяти.
     */
    @Test
    void testProcessBotUpdate_WithGoogleSheetsException() throws DaoException {
        BotUpdate botUpdate =
                new BotUpdate(chatId, CommandData.START_COMMAND.getReadableName(), null, null);
        Mockito.when(commandHandlerMock.handleCommand(CommandData.START_COMMAND.getReadableName(), chatId)).
                thenThrow(new GoogleSheetsException("exception", new Exception("exception")));

        List<AnswerMessage> response = botUpdateProcessor.processBotUpdate(botUpdate);
        List<AnswerMessage> expected = List.of(
                new AnswerMessage("""
                        С вашей гугл-таблицей что-то не так \uD83E\uDEE3
                        Проверьте, что ваша таблица соответствует требованиям:
                        1. Она должна быть открыта
                        2. У бота должны быть права редактора
                        3. В таблице должно быть 3 листа "Общая информация", "Расходы", "Доходы"
                        """, chatId),
                new AnswerMessage("Вы будете переведены на режим работы с данными в памяти." +
                        " Данные, который вы заполняли в гугл-таблице, будут утеряны.", chatId)
        );

        Mockito.verify(userServiceMock).setDataType(chatId, DataType.IN_MEMORY);
        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(2);
    }

    /**
     * Тест для проверки обработки обновления с исключением {@link DaoException}.
     * Ожидается, что будет возвращено сообщение об ошибке.
     */
    @Test
    void testProcessBotUpdate_WithDaoException() throws DaoException {
        BotUpdate botUpdate =
                new BotUpdate(chatId, CommandData.START_COMMAND.getReadableName(), null, null);
        Mockito.when(commandHandlerMock.handleCommand(CommandData.START_COMMAND.getReadableName(), chatId)).
                thenThrow(new DaoException(new Exception("exception")));

        List<AnswerMessage> response = botUpdateProcessor.processBotUpdate(botUpdate);
        List<AnswerMessage> expected = List.of(new AnswerMessage("Произошла какая-то ошибка", chatId));

        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тест для проверки обработки обновления с неподдерживаемым сообщением.
     * Ожидается, что будет возвращено сообщение о неподдерживаемом типе сообщения.
     */
    @Test
    void testProcessBotUpdate_WithUnsupportedMessage() {
        BotUpdate botUpdate = new BotUpdate(chatId, null, null, null);

        List<AnswerMessage> response = botUpdateProcessor.processBotUpdate(botUpdate);
        List<AnswerMessage> expected = List.of(new AnswerMessage("Неподдерживаемое сообщение", chatId));

        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}