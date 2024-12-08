package ru.naumen.bot.data.dao;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.data.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Класс, предоставляющий функционал для управления данными о пользователях в памяти.
 *
 * <p>Использует HashMap для хранения данных пользователей.
 */
@Component
public class UserDao {

    /**
     * Хранилище для данных пользователей, где ключом является идентификатор чата,
     * а значением — объект {@link User}.
     */
    private final Map<Long, User> users = new HashMap<>();


    /**
     * Проверяет, открыт ли чат для данного идентификатора.
     *
     * @param chatId идентификатор чата, который нужно проверить.
     * @return true, если чат открыт, иначе false.
     */
    public boolean checkChat(long chatId) {
        return users.containsKey(chatId);
    }

    /**
     * Открывает чат для данного идентификатора.
     * При открытии чата создается новый объект {@link User} и добавляется в хранилище.
     * По умолчанию чат открыт в состоянии {@link ChatState#WAITING_FOR_TYPE_DB} для последующего выбора
     * предпочтительного хранилища и с типом данных {@link DataType#IN_MEMORY},
     * на случай если пользователь не выберет тип данных и начнет работать с ботом.
     *
     * @param chatId идентификатор чата, который нужно открыть.
     */
    public void openChat(long chatId) {
        users.put(chatId, new User(DataType.IN_MEMORY, ChatState.WAITING_FOR_TYPE_DB));
    }

    /**
     * Устанавливает состояние чата для указанного идентификатора.
     *
     * @param chatId    идентификатор чата, для которого необходимо установить состояние
     * @param chatState состояние, которое необходимо установить
     */
    public void setChatState(long chatId, ChatState chatState) {
        users.get(chatId).setChatState(chatState);
    }

    /**
     * Возвращает состояние чата для указанного идентификатора.
     *
     * @param chatId идентификатор чата, для которого необходимо получить состояние
     * @return состояние чата
     */
    public ChatState getChatState(long chatId) {
        return users.get(chatId).getChatState();
    }

    /**
     * Устанавливает тип данных для указанного идентификатора.
     *
     * @param chatId   идентификатор чата, для которого необходимо установить тип данных
     * @param dataType тип данных, который необходимо установить
     */
    public void setDataType(long chatId, DataType dataType) {
        users.get(chatId).setDataType(dataType);
    }

    /**
     * Возвращает тип данных для указанного идентификатора.
     *
     * @param chatId идентификатор чата, для которого необходимо получить тип данных
     * @return тип данных
     */
    public DataType getDataType(long chatId) {
        return users.get(chatId).getDataType();
    }

    /**
     * Устанавливает идентификатор Google Sheet для указанного идентификатора.
     *
     * @param chatId        идентификатор чата, для которого необходимо установить ссылку
     * @param googleSheetId ссылка на Google Sheet
     */
    public void setGoogleSheetId(long chatId, String googleSheetId) {
        users.get(chatId).setGoogleSheetId(googleSheetId);
    }

    /**
     * Возвращает идентификатор Google Sheet для указанного идентификатора.
     *
     * @param chatId идентификатор чата, для которого необходимо получить ссылку
     * @return идентификатор Google Sheet
     */
    public String getGoogleSheetId(long chatId) {
        return users.get(chatId).getGoogleSheetId();
    }

    /**
     * Возвращает идентификаторы всех открытых чатов.
     */
    public Set<Long> getUsersId() {
        return users.keySet();
    }
}