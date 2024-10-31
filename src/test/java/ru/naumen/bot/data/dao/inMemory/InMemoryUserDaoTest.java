package ru.naumen.bot.data.dao.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для класса {@link InMemoryUserDao}, проверяющие функциональность управления пользователями.
 */
public class InMemoryUserDaoTest {

    /**
     * Тестируемый объект {@link InMemoryUserDao}, который проверяется в данном тестовом классе.
     */
    private InMemoryUserDao userDao;

    /**
     * Инициализация {@link InMemoryUserDao} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userDao = new InMemoryUserDao();
    }

    /**
     * Тест для проверки, что метод {@link InMemoryUserDao#checkChat(long)} возвращает false,
     * когда чат не открыт.
     */
    @Test
    void testCheckChatReturnsFalseWhenChatIsNotOpened() {
        long chatId = 12345L;

        assertThat(userDao.checkChat(chatId)).isFalse();
    }

    /**
     * Тест для проверки, что метод {@link InMemoryUserDao#checkChat(long)} возвращает true,
     * когда чат открыт.
     */
    @Test
    void testCheckChatReturnsTrueWhenChatIsOpened() {
        long chatId = 12345L;
        userDao.openChat(chatId);

        assertThat(userDao.checkChat(chatId)).isTrue();
    }

}

