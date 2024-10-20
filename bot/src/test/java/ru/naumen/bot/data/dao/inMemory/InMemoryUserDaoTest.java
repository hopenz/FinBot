package ru.naumen.bot.data.dao.inMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryUserDaoTest {

    private InMemoryUserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new InMemoryUserDao();
    }

    @Test
    void testCheckChatReturnsFalseWhenChatIsNotOpened() {
        long chatId = 12345L;

        assertThat(userDao.checkChat(chatId)).isFalse();
    }

    @Test
    void testCheckChatReturnsTrueWhenChatIsOpened() {
        long chatId = 12345L;
        userDao.openChat(chatId);

        assertThat(userDao.checkChat(chatId)).isTrue();
    }

}

