package ru.naumen.bot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryBalanceDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryExpenseDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryIncomeDao;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;

/**
 * Сервис UserService предоставляет методы для работы с данными о пользователях.
 */
@Service
public class UserService {

    /**
     * Начало GoogleSheets id в ссылке.
     */
    private static final String START_SHEET_ID = "/d/";

    /**
     * Конец GoogleSheets id в ссылке.
     */
    private static final String END_SHEET_ID = "/edit";

    /**
     * DAO для работы с пользователями.
     */
    private final UserDao userDao;

    /**
     * DAO для управления балансом пользователя в памяти.
     */
    private final InMemoryBalanceDao inMemoryBalanceDao;

    /**
     * DAO для управления расходами пользователя в памяти.
     */
    private final InMemoryExpenseDao inMemoryExpenseDao;

    /**
     * DAO для управления доходами пользователя в памяти.
     */
    private final InMemoryIncomeDao inMemoryIncomeDao;

    /**
     * Конструктор UserService. Инициализирует сервис с объектами DAO.
     *
     * @param userDao            DAO для работы с пользователями.
     * @param inMemoryBalanceDao DAO для управления балансом пользователя в памяти.
     * @param inMemoryExpenseDao DAO для управления расходами пользователя в памяти.
     * @param inMemoryIncomeDao  DAO для управления доходами пользователя в памяти.
     */
    public UserService(UserDao userDao, InMemoryBalanceDao inMemoryBalanceDao,
                       InMemoryExpenseDao inMemoryExpenseDao, InMemoryIncomeDao inMemoryIncomeDao) {
        this.userDao = userDao;
        this.inMemoryBalanceDao = inMemoryBalanceDao;
        this.inMemoryExpenseDao = inMemoryExpenseDao;
        this.inMemoryIncomeDao = inMemoryIncomeDao;
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
        inMemoryBalanceDao.setBalance(chatId, 0.0);
        inMemoryExpenseDao.createUserList(chatId);
        inMemoryIncomeDao.createUserList(chatId);
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
     * @param chatId идентификатор чата
     * @param link   ссылка на Google Sheets
     */
    public void setGoogleSheetId(long chatId, String link) {
        String googleSheetId = link.substring(link.indexOf(START_SHEET_ID) + START_SHEET_ID.length(),
                link.indexOf(END_SHEET_ID));
        userDao.setGoogleSheetId(chatId, googleSheetId);
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
    public String getGoogleSheetId(Long chatId) {
        return userDao.getGoogleSheetId(chatId);
    }
}