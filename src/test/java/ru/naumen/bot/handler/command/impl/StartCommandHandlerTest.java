package ru.naumen.bot.handler.command.impl;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.service.UserService;

import java.util.Arrays;
import java.util.List;

/**
 * Тесты для класса {@link StartCommandHandler}, проверяющие обработку команды "/start".
 */
public class StartCommandHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для работы с данными пользователей.
     */
    private UserService userServiceMock;

    /**
     * Тестируемый объект {@link CommandHandler}, обрабатывающий команду "/start".
     */
    private CommandHandler commandHandler;

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345;

    /**
     * Инициализация всех зависимостей и {@link StartCommandHandler} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userServiceMock = Mockito.mock(UserService.class);
        commandHandler = new StartCommandHandler(userServiceMock);
    }

    /**
     * Тест для проверки обработки команды "/start" для уже открытого чата.
     * Проверяет, что пользователь получает приветственное сообщение без повторного открытия чата.
     */
    @Test
    void testHandleCommandWithOpenChat() throws DaoException {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(true);
        List<AnswerMessage> expected = List.of(new AnswerMessage(
                "Ещё раз здравствуйте, чтобы ознакомиться с командами - напишите " +
                        Commands.HELP_COMMAND.getCommand(), chatId));

        List<AnswerMessage> response = commandHandler.handleCommand(Commands.START_COMMAND.getCommand(), chatId);

        Mockito.verify(userServiceMock).isChatOpened(chatId);
        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.NOTHING_WAITING);
        Mockito.verifyNoMoreInteractions(userServiceMock);

        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }

    /**
     * Тест для проверки обработки команды "/start" для нового чата.
     * Проверяет, что чат открывается, а пользователь получает сообщение с выбором способа хранения данных.
     */
    @Test
    void testHandleCommandWithNotOpenChat() throws DaoException {
        Mockito.when(userServiceMock.isChatOpened(chatId)).thenReturn(false);
        List<String> keyboardButtons = Arrays.stream(TypeDBKeyboard.values())
                .map(TypeDBKeyboard::getData)
                .toList();
        List<AnswerMessage> expected = List.of(new AnswerMessage(
                "Здравствуйте! Как вы хотите хранить данные?", chatId, List.of(keyboardButtons)));

        List<AnswerMessage> response = commandHandler.handleCommand(Commands.START_COMMAND.getCommand(), chatId);

        Mockito.verify(userServiceMock).isChatOpened(chatId);
        Mockito.verify(userServiceMock).openChat(chatId);
        Mockito.verifyNoMoreInteractions(userServiceMock);

        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}