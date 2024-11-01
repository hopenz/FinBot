package ru.naumen.bot.telegramBot.processor;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        updateBotProcessor = new UpdateBotProcessor(messageBotProcessor, commandBotProcessor);
    }

    /**
     * Тест для {@link UpdateBotProcessor#processUpdate(Update)}.
     * Проверяет, что при получении не стартовой команды и неактивном чате
     * пользователю отправляется сообщение с предложением нажать /start.
     */
    @Test
    void testProcessUpdate_NotStartCommand_UserNotOpenedChat() {
        Update update = createUpdate(12345L, HELP_COMMAND);
        when(commandBotProcessor.isChatActiveOrStarting(HELP_COMMAND, 12345L)).thenReturn(false);

        updateBotProcessor.processUpdate(update);

        verify(commandBotProcessor).isChatActiveOrStarting(HELP_COMMAND, 12345L);
        verifyNoMoreInteractions(messageBotProcessor);
        verifyNoMoreInteractions(commandBotProcessor);
    }

    /**
     * Тест для {@link UpdateBotProcessor#processUpdate(Update)}.
     * Проверяет, что при получении стартовой команды и неактивном чате
     * вызывается обработка команды без дополнительных сообщений.
     */
    @Test
    void testProcessUpdate_StartCommand_ChatActiveOrStarting() {
        Update update = createUpdate(12345L, START_COMMAND);
        when(commandBotProcessor.isChatActiveOrStarting(START_COMMAND, 12345L)).thenReturn(true);

        updateBotProcessor.processUpdate(update);

        verify(commandBotProcessor).isChatActiveOrStarting(START_COMMAND, 12345L);
        verify(commandBotProcessor).processCommand(START_COMMAND, 12345L);
        verifyNoMoreInteractions(messageBotProcessor);
        verifyNoMoreInteractions(commandBotProcessor);
    }

    /**
     * Тест для {@link UpdateBotProcessor#processUpdate(Update)}.
     * Проверяет корректную обработку команды, если чат активен.
     */
    @Test
    void testProcessUpdate_CommandMessage_ChatActiveOrStarting() {
        Update update = createUpdate(12345L, "/command");
        when(commandBotProcessor.isChatActiveOrStarting("/command", 12345L)).thenReturn(true);

        updateBotProcessor.processUpdate(update);

        verify(commandBotProcessor).isChatActiveOrStarting("/command", 12345L);
        verify(commandBotProcessor).processCommand("/command", 12345L);
        verifyNoMoreInteractions(messageBotProcessor);
        verifyNoMoreInteractions(commandBotProcessor);
    }

    /**
     * Тест для {@link UpdateBotProcessor#processUpdate(Update)}.
     * Проверяет корректную обработку текстового сообщения, если чат активен.
     */
    @Test
    void testProcessUpdate_NormalMessage_ChatActiveOrStarting() {
        Update update = createUpdate(12345L, "hello");
        when(commandBotProcessor.isChatActiveOrStarting("hello", 12345L)).thenReturn(true);

        updateBotProcessor.processUpdate(update);

        verify(commandBotProcessor).isChatActiveOrStarting("hello", 12345L);
        verify(messageBotProcessor).processMessage("hello", 12345L);
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

        verifyNoMoreInteractions(messageBotProcessor);
        verifyNoMoreInteractions(commandBotProcessor);
    }

    /**
     * Вспомогательный метод для создания объекта {@link Update} с заданными параметрами.
     *
     * @param chatId      идентификатор чата
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
