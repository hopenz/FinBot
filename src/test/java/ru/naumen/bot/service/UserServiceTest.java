package ru.naumen.bot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.UserDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryExpenseDao;
import ru.naumen.bot.data.dao.inMemory.InMemoryIncomeDao;

/**
 * Тесты для класса {@link UserService}, проверяющие корректность обработки операций с пользователями.
 */
public class UserServiceTest {

    /**
     * Мок-объект для {@link UserDao}, используемый для работы с данными пользователями.
     */
    private UserDao userDaoMock;

    /**
     * Мок-объект для {@link InMemoryIncomeDao}, используемый для работы с доходами пользователей.
     */
    private InMemoryIncomeDao incomeDaoMock;

    /**
     * Мок-объект для {@link InMemoryExpenseDao}, используемый для работы с расходами пользователей.
     */
    private InMemoryExpenseDao expenseDaoMock;

    /**
     * Тестируемый объект {@link UserService}, который проверяется в данном тестовом классе.
     */
    private UserService userService;

    /**
     * Мок-объект для {@link BalanceDao}, используемый для управления балансом пользователей.
     */
    private BalanceDao balanceDaoMock;

    /**
     * Инициализация всех зависимостей и {@link UserService} перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        userDaoMock = Mockito.mock(UserDao.class);
        incomeDaoMock = Mockito.mock(InMemoryIncomeDao.class);
        expenseDaoMock = Mockito.mock(InMemoryExpenseDao.class);
        balanceDaoMock = Mockito.mock(BalanceDao.class);
        userService = new UserService(userDaoMock, incomeDaoMock, expenseDaoMock, balanceDaoMock);
    }

    /**
     * Тест для открытия чата, когда чат еще не открыт. Проверяет,
     * что методы открытия чата и создания списков доходов и расходов вызываются.
     */
    @Test
    void testOpenChat_whenChatIsNotOpened() {
        Mockito.when(userDaoMock.checkChat(12345L)).thenReturn(false);
        userService.openChat(12345L);

        Mockito.verify(userDaoMock).checkChat(12345L);
        Mockito.verify(userDaoMock).openChat(12345L);
        Mockito.verify(incomeDaoMock).createUserList(12345L);
        Mockito.verify(expenseDaoMock).createUserList(12345L);
        Mockito.verify(balanceDaoMock).setBalance(12345L, 0.0);
    }

    /**
     * Тест для открытия чата, когда чат уже открыт. Проверяет,
     * что методы открытия чата не вызываются.
     */
    @Test
    void testOpenChat_whenChatIsOpened() {
        Mockito.when(userDaoMock.checkChat(12345L)).thenReturn(true);
        userService.openChat(12345L);

        Mockito.verify(userDaoMock).checkChat(12345L);
        Mockito.verifyNoMoreInteractions(userDaoMock);
        Mockito.verifyNoMoreInteractions(incomeDaoMock);
        Mockito.verifyNoMoreInteractions(expenseDaoMock);
        Mockito.verifyNoMoreInteractions(balanceDaoMock);
    }
}
