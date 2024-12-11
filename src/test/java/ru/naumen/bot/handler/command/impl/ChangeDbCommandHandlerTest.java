package ru.naumen.bot.handler.command.impl;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.interaction.CommandData;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.service.UserService;

import java.util.Arrays;
import java.util.List;

/**
 * Тесты для класса {@link ChangeDbCommandHandler}, проверяющие обработку команды "/changedb".
 */
public class ChangeDbCommandHandlerTest {

    /**
     * Мок-объект для {@link UserService}, используемый для управления состоянием пользователя.
     */
    private final UserService userServiceMock = Mockito.mock(UserService.class);

    /**
     * Тестируемый объект {@link ChangeDbCommandHandler}, обрабатывающий команду "/changedb".
     */
    private final ChangeDbCommandHandler changeDbCommandHandler = new ChangeDbCommandHandler(userServiceMock);

    /**
     * Тест для обработки команды "/changedb".
     * Проверяет, что бот отправляет сообщение с предложением выбрать базу данных.
     */
    @Test
    void testHandleCommand() {
        long chatId = 12345L;
        List<String> keyboardButtons = Arrays.stream(TypeDBKeyboard.values())
                .map(TypeDBKeyboard::getData)
                .toList();
        List<AnswerMessage> expected =
                List.of(new AnswerMessage("Выберите базу данных", chatId, List.of(keyboardButtons)));
        List<AnswerMessage> response =
                changeDbCommandHandler.handleCommand(CommandData.CHANGE_DB_COMMAND.getReadableName(), chatId);

        Mockito.verify(userServiceMock).setUserState(chatId, ChatState.WAITING_FOR_TYPE_DB_FOR_CHANGE_DB);
        Assertions.assertThat(response).containsAll(expected);
        Assertions.assertThat(response.size()).isEqualTo(1);
    }
}