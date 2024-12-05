package ru.naumen.bot.handler.callback.impl;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.callback.CallbackHandler;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.service.DatabaseService;
import ru.naumen.bot.service.UserService;

import java.util.List;

/**
 * Обработчик коллбеков для изменения типа хранилища данных
 */
@Component
public class TypeDBForChangeCallbackHandler implements CallbackHandler {

    /**
     * Сервис для работы с базой данных.
     */
    private final DatabaseService databaseService;

    /**
     * Сервис для работы с пользователем.
     */
    private final UserService userService;

    /**
     * Конструктор для инициализации обработчика.
     *
     * @param databaseService сервис для работы с базой данных.
     * @param userService     Сервис для работы с пользователем.
     */
    public TypeDBForChangeCallbackHandler(DatabaseService databaseService, UserService userService) {
        this.databaseService = databaseService;
        this.userService = userService;
    }

    @Override
    public ChatState getChatState() {
        return ChatState.WAITING_FOR_TYPE_DB_FOR_CHANGE_DB;
    }

    @Override
    public List<AnswerMessage> handleCallback(String callbackData, String callbackId, long chatId) throws DaoException {
        DataType dataType = callbackData.equals(TypeDBKeyboard.GOOGLE_SHEETS.getData())
                ? DataType.IN_GOOGLE_SHEET
                : DataType.IN_MEMORY;

        if (userService.getDataType(chatId).equals(dataType)) {
            return List.of(new AnswerMessage("Вы уже используете этот способ хранения", chatId));
        }

        if (userService.getGoogleSheetId(chatId) == null) {
            userService.setUserState(chatId, ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);
            return List.of(new AnswerMessage("""
                    Давайте создадим таблицу и привяжем к ней бота:
                    1. Для создания таблицы перейдите по ссылке https://sheets.new/
                    2. Добавьте finbotaccount@celtic-house-440906-m1.iam.gserviceaccount.com в качестве пользователя-редактор
                    3. Пришлите мне ссылку на новую созданную таблицу (просто скопируйте и отправьте мне), остальное я сделаю сам
                    """, chatId));
        }

        databaseService.changeDB(chatId, dataType);
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        return List.of(new AnswerMessage("Теперь ваши данные хранятся в '" + callbackData + "'!", chatId));
    }
}
