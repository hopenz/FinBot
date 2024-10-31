package ru.naumen.bot.telegramBot.processor;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.telegramBot.controller.TelegramBotController;
import ru.naumen.bot.telegramBot.service.UserService;

import static org.mockito.Mockito.*;
import static ru.naumen.bot.telegramBot.command.Commands.HELP_COMMAND;
import static ru.naumen.bot.telegramBot.command.Commands.START_COMMAND;

/**
 * Тестовый класс для {@link UpdateBotProcessor}, предназначенный для проверки
 * корректности обработки различных типов обновлений телеграм.
 */
public class UpdateBotProcessorTest {

    /**
     * Мок-объект для {@link MessageBotProcessor}, который используется для обработки текстовых сообщений.
     */
    private MessageBotProcessor messageBotProcessor;

    /**
     * Мок-объект для {@link CommandBotProcessor}, который используется для обработки команд.
     */
    private CommandBotProcessor commandBotProcessor;

    /**
     * Мок-объект для {@link UserService}, используемый для проверки статуса чата
     */
    private UserService userService;

    /**
     * Мок-объект для {@link TelegramBotController}, используемый для отправки сообщений пользователям.
     */
    private TelegramBotController telegramBotController;

    /**
     * Тестируемый объект {@link UpdateBotProcessor}, который проверяется в данном тестовом классе.
     */
    private UpdateBotProcessor updateBotProcessor;

    /**
     * Инициализация всех зависимостей и updateBotProcessor перед каждым тестом
     */
    @BeforeEach
    public void setUp() {
        messageBotProcessor = mock(MessageBotProcessor.class);
        commandBotProcessor = mock(CommandBotProcessor.class);
        userService = mock(UserService.class);
        telegramBotController = mock(TelegramBotController.class);
        updateBotProcessor = new UpdateBotProcessor(messageBotProcessor, commandBotProcessor, userService, telegramBotController);
    }

    /**
     * Тест для {@link UpdateBotProcessor#processUpdate(Update)}.
     * Проверяет, что при получении не стартовой команды и неактивном чате
     * пользователю отправляется сообщение с предложением нажать /start.
     */
    @Test
    void testProcessUpdate_NotStartCommand_UserNotOpenedChat() {
        Update update = createUpdate(12345L, HELP_COMMAND);
        when(userService.isChatOpened(12345L)).thenReturn(false);

        updateBotProcessor.processUpdate(update);

        verify(telegramBotController).sendMessage("Чтобы начать работу, нажмите /start", 12345L);
        verifyNoMoreInteractions(messageBotProcessor);
        verifyNoMoreInteractions(commandBotProcessor);
    }

    /**
     * Тест для {@link UpdateBotProcessor#processUpdate(Update)}.
     * Проверяет, что при получении стартовой команды и неактивном чате
     * вызывается обработка команды без дополнительных сообщений.
     */
    @Test
    void testProcessUpdate_StartCommand_UserNotOpenedChat() {
        Update update = createUpdate(12345L, START_COMMAND);
        when(userService.isChatOpened(12345L)).thenReturn(false);

        updateBotProcessor.processUpdate(update);

        verify(commandBotProcessor).processCommand(START_COMMAND, 12345L);
        verifyNoMoreInteractions(telegramBotController);
        verifyNoMoreInteractions(messageBotProcessor);
        verifyNoMoreInteractions(commandBotProcessor);
    }

    /**
     * Тест для {@link UpdateBotProcessor#processUpdate(Update)}.
     * Проверяет корректную обработку команды, если чат активен.
     */
    @Test
    void testProcessUpdate_CommandMessage() {
        Update update = createUpdate(12345L, "/command");
        when(userService.isChatOpened(12345L)).thenReturn(true);

        updateBotProcessor.processUpdate(update);

        verify(commandBotProcessor).processCommand("/command", 12345L);
        verifyNoMoreInteractions(telegramBotController);
        verifyNoMoreInteractions(messageBotProcessor);
        verifyNoMoreInteractions(commandBotProcessor);
    }

    /**
     * Тест для {@link UpdateBotProcessor#processUpdate(Update)}.
     * Проверяет корректную обработку текстового сообщения, если чат активен.
     */
    @Test
    void testProcessUpdate_NormalMessage() {
        Update update = createUpdate(12345L, "hello");
        when(userService.isChatOpened(12345L)).thenReturn(true);

        updateBotProcessor.processUpdate(update);

        verify(messageBotProcessor).processMessage("hello", 12345L);
        verifyNoMoreInteractions(telegramBotController);
        verifyNoMoreInteractions(messageBotProcessor);
        verifyNoMoreInteractions(commandBotProcessor);
    }

    /**
     * Тест для {@link UpdateBotProcessor#processUpdate(Update)}.
     * Проверяет, что при отсутствии текста в сообщении не выполняются никакие действия.
     */
    @Test
    void testProcessUpdate_NullMessage() {
        Update update = createUpdate(12345L, null);

        updateBotProcessor.processUpdate(update);

        verifyNoMoreInteractions(telegramBotController);
        verifyNoMoreInteractions(messageBotProcessor);
        verifyNoMoreInteractions(commandBotProcessor);
        verifyNoMoreInteractions(userService);
    }

    /**
     * Вспомогательный метод для создания объекта {@link Update} с заданными параметрами.
     *
     * @param chatId идентификатор чата
     * @param messageText текст сообщения
     * @return объект {@link Update} с заданными параметрами
     */
    private Update createUpdate(long chatId, String messageText) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(chatId);
        when(message.text()).thenReturn(messageText);

        return update;
    }

}
