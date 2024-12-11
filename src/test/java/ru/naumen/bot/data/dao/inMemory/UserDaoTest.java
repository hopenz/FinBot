package ru.naumen.bot.data.dao.inMemory;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.enums.ChatState;
import ru.naumen.bot.data.enums.DataType;

/**
 * Тесты для класса {@link UserDao}, проверяющие функциональность управления пользователями.
 */
public class UserDaoTest {

    /**
     * Тестируемый объект {@link UserDao}, который проверяется в данном тестовом классе.
     */
    private UserDao userDao;

    /**
     * Идентификатор чата, в котором происходит тестирование.
     */
    private final long chatId = 12345L;

    /**
     * Инициализация {@link UserDao} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userDao = new UserDao();
    }

    /**
     * Тест для проверки, что метод {@link UserDao#checkChat(long)} возвращает false
     * для нового чата, который ещё не был открыт.
     */
    @Test
    void testCheckChatReturnsFalseForNewChat() {
        Assertions.assertThat(userDao.checkChat(chatId)).isFalse();
    }

    /**
     * Тест для открытия чата и проверки, что метод {@link UserDao#checkChat(long)}
     * возвращает true после открытия чата.
     */
    @Test
    void testOpenChatAndCheckChat() {
        userDao.openChat(chatId);

        Assertions.assertThat(userDao.checkChat(chatId)).isTrue();
    }

    /**
     * Тест для проверки, что метод {@link UserDao#getChatState(long)}
     * возвращает состояние {@link ChatState#WAITING_FOR_TYPE_DB} после открытия чата.
     */
    @Test
    void testGetChatStateReturnsDefaultStateAfterOpenChat() {
        userDao.openChat(chatId);

        Assertions.assertThat(userDao.getChatState(chatId)).isEqualTo(ChatState.WAITING_FOR_TYPE_DB);
    }

    /**
     * Тест для установки и получения состояния чата. Проверяет, что состояние
     * корректно обновляется и возвращается методом {@link UserDao#getChatState(long)}.
     */
    @Test
    void testSetAndGetChatState() {
        userDao.openChat(chatId);

        userDao.setChatState(chatId, ChatState.NOTHING_WAITING);

        Assertions.assertThat(userDao.getChatState(chatId)).isEqualTo(ChatState.NOTHING_WAITING);
    }

    /**
     * Тест для проверки, что метод {@link UserDao#getDataType(long)} возвращает
     * тип данных {@link DataType#IN_MEMORY} после открытия чата.
     */
    @Test
    void testGetDataTypeReturnsDefaultAfterOpenChat() {
        userDao.openChat(chatId);

        Assertions.assertThat(userDao.getDataType(chatId)).isEqualTo(DataType.IN_MEMORY);
    }

    /**
     * Тест для установки и получения типа данных. Проверяет, что тип данных
     * корректно обновляется и возвращается методом {@link UserDao#getDataType(long)}.
     */
    @Test
    void testSetAndGetDataType() {
        userDao.openChat(chatId);

        userDao.setDataType(chatId, DataType.IN_GOOGLE_SHEET);

        Assertions.assertThat(userDao.getDataType(chatId)).isEqualTo(DataType.IN_GOOGLE_SHEET);
    }

    /**
     * Тест для установки и получения ссылки на Google Sheet. Проверяет, что ссылка
     * корректно обновляется и возвращается методом {@link UserDao#getGoogleSheetId(long)}.
     */
    @Test
    void testSetAndGetGoogleSheetId() {
        String googleSheetLink = "https://docs.google.com/spreadsheets/d/123456789";

        userDao.openChat(chatId);
        userDao.setGoogleSheetId(chatId, googleSheetLink);

        Assertions.assertThat(userDao.getGoogleSheetId(chatId)).isEqualTo(googleSheetLink);
    }

    /**
     * Тест для проверки, что метод {@link UserDao#getGoogleSheetId(long)} возвращает
     * null, если ссылка на Google Sheet не была установлена.
     */
    @Test
    void testGetGoogleSheetIdReturnsNullIfNotSet() {
        userDao.openChat(chatId);

        Assertions.assertThat(userDao.getGoogleSheetId(chatId)).isNull();
    }

}

