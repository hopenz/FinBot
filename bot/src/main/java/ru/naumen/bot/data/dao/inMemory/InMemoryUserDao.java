package ru.naumen.bot.data.dao.inMemory;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.DataType;
import ru.naumen.bot.data.dao.UserDao;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс InMemoryUserDao реализует интерфейс UserDao и предоставляет
 * функционал для управления данными пользователей в памяти.
 *
 * <p>Использует HashMap для хранения состояния чата пользователей.
 */
@Component
public class InMemoryUserDao implements UserDao {

    /**
     * Хранилище для данных пользователей, где ключом является идентификатор чата,
     * а значением — тип данных, используемый для этого чата.
     */
    private final Map<Long, DataType> usersDataType = new HashMap<>();

    /**
     * DAO для управления доходами пользователей.
     */
    private final InMemoryIncomeDao incomeDao;

    /**
     * DAO для управления расходами пользователей.
     */
    private final InMemoryExpenseDao expenseDao;

    /**
     * Конструктор класса InMemoryUserDao.
     *
     * @param incomeDao  DAO для работы с доходами пользователей.
     * @param expenseDao DAO для работы с расходами пользователей.
     */
    public InMemoryUserDao(InMemoryIncomeDao incomeDao, InMemoryExpenseDao expenseDao) {
        this.incomeDao = incomeDao;
        this.expenseDao = expenseDao;
    }

    /**
     * Проверяет, открыт ли чат для данного идентификатора.
     *
     * @param chatId идентификатор чата, который нужно проверить.
     * @return true, если чат открыт, иначе false.
     */
    @Override
    public boolean checkChat(long chatId) {
        return usersDataType.containsKey(chatId);
    }

    /**
     * Открывает чат для данного идентификатора.
     * При открытии чата также создаются списки доходов и расходов
     * для пользователя.
     *
     * @param chatId идентификатор чата, который нужно открыть.
     */
    @Override
    public void openChat(long chatId) {
        if (!checkChat(chatId)) {
            usersDataType.put(chatId, DataType.IN_MEMORY);
            incomeDao.createUserList(chatId);
            expenseDao.createUserList(chatId);
        }
    }
}
