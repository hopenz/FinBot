package ru.naumen.bot.handler.callback.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.handler.callback.CallbackHandler;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик коллбеков выбора типа хранилища данных при старте взаимодействия с ботом.
 */
@Component
public class TypeDBForStartCallbackHandler implements CallbackHandler {

    /**
     * Сервис для работы с пользователем.
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param userService Сервис для работы с пользователем.
     */
    public TypeDBForStartCallbackHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ChatState getChatState() {
        return ChatState.WAITING_FOR_TYPE_DB;
    }

    @Override
    public List<AnswerMessage> handleCallback(String callbackData, String callbackId, long chatId) {
        List<AnswerMessage> answerMessages = new ArrayList<>(List.of(
                new AnswerMessage("Помните, вы можете поменять способ хранения данных с помощью команды " +
                        Commands.CHANGE_DB_COMMAND.getCommand(), chatId)));

        if (callbackData.equals(TypeDBKeyboard.GOOGLE_SHEETS.getData())) {
            userService.setUserState(chatId, ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);
            answerMessages.add(new AnswerMessage("""
                    Давайте создадим таблицу и привяжем к ней бота:
                    1. Для создания таблицы перейдите по ссылке https://sheets.new/
                    2. Добавьте finbotaccount@celtic-house-440906-m1.iam.gserviceaccount.com в качестве пользователя-редактор
                    3. Пришлите мне ссылку на новую созданную таблицу (просто скопируйте и отправьте мне), остальное я сделаю сам
                    """, chatId));
        } else {
            userService.setUserState(chatId, ChatState.NOTHING_WAITING);
            answerMessages.add(
                    new AnswerMessage("Теперь ваши данные хранятся в оперативной памяти!", chatId));
        }
        return answerMessages;
    }
}
