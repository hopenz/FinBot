package ru.naumen.bot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.inMemory.InMemoryUserDao;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;

/**
 * Сервис UserService предоставляет методы для работы с данными о пользователях.
 */
@Service
public class UserService {

    /**
     * DAO для работы с пользователями.
     */
    private final InMemoryUserDao userDao;

    /**
     * Сервис для взаимодействия с базами данных.
     */
    private final DatabaseService databaseService;

    /**
     * Конструктор UserService. Инициализирует сервис с объектами DAO.
     *
     * @param userDao         DAO для работы с пользователями.
     * @param databaseService Сервис для взаимодействия с базами данных.
     */
    public UserService(InMemoryUserDao userDao, DatabaseService databaseService) {
        this.userDao = userDao;
        this.databaseService = databaseService;
    }

    /**
     * Проверяет, открыт ли чат для текущего пользователя.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     * @return true, если чат открыт, иначе false.
     */
    public boolean isChatOpened(long chatId) {
        return userDao.checkChat(chatId);
    }

    /**
     * Открывает чат для текущего пользователя.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     */
    public void openChat(long chatId) {
        userDao.openChat(chatId);
        databaseService.createInMemoryDB(chatId);
        userDao.setChatState(chatId, ChatState.WAITING_FOR_TYPE_DB);
    }

    /**
     * Устанавливает состояние чата для указанного идентификатора.
     *
     * @param chatId    идентификатор чата, для которого необходимо установить состояние
     * @param chatState состояние, которое необходимо установить
     */
    public void setUserState(long chatId, ChatState chatState) {
        userDao.setChatState(chatId, chatState);
    }

    /**
     * Возвращает состояние чата для указанного идентификатора.
     *
     * @param chatId идентификатор чата
     * @return состояние чата
     */
    public ChatState getUserState(Long chatId) {
        return userDao.getChatState(chatId);
    }

    /**
     * Возвращает тип базы данных для указанного идентификатора.
     *
     * @param chatId идентификатор чата
     * @return тип базы данных
     */
    public DataType getDataType(long chatId) {
        return userDao.getDataType(chatId);
    }

    /**
     * Устанавливает ссылку на Google Sheets для указанного идентификатора.
     *
     * @param chatId  идентификатор чата
     * @param message ссылка на Google Sheets
     */
    public void setGoogleSheetLink(long chatId, String message) {
        userDao.setGoogleSheetLink(chatId, message);
    }

    /**
     * Устанавливает тип базы данных для указанного идентификатора.
     *
     * @param chatId   идентификатор чата
     * @param dataType тип базы данных
     */
    public void setDataType(long chatId, DataType dataType) {
        userDao.setDataType(chatId, dataType);
    }

    /**
     * Возвращает ссылку на Google Sheets для указанного идентификатора.
     *
     * @param chatId идентификатор чата
     * @return ссылка на Google Sheets
     */
    public String getGoogleSheetLink(Long chatId) {
        return userDao.getGoogleSheetLink(chatId);
    }
}