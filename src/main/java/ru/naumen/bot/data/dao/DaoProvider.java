package ru.naumen.bot.data.dao;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.googleSheets.GoogleSheetsDaoGroup;
import ru.naumen.bot.data.dao.inMemory.InMemoryDaoGroup;
import ru.naumen.bot.data.entity.DataType;

/**
 * Класс DaoProvider предоставляет доступ к DAO-объектам для работы с данными пользователей.
 */
@Component
public class DaoProvider {

    /**
     * Компонент, объединяющий DAO для операций в оперативной памяти.
     */
    private final InMemoryDaoGroup inMemoryDaoGroup;

    /**
     * Компонент, объединяющий DAO для операций с Google Sheets.
     */
    private final GoogleSheetsDaoGroup googleSheetsDaoGroup;

    /**
     * Конструктор класса DaoProvider, инициализирующий компоненты для операций в оперативной памяти и в Google Sheets.
     *
     * @param inMemoryDaoGroup компонент с DAO для операций в оперативной памяти
     * @param googleSheetsDaoGroup компонент с DAO для операций с Google Sheets
     */
    public DaoProvider(InMemoryDaoGroup inMemoryDaoGroup, GoogleSheetsDaoGroup googleSheetsDaoGroup) {
        this.inMemoryDaoGroup = inMemoryDaoGroup;
        this.googleSheetsDaoGroup = googleSheetsDaoGroup;
    }

    /**
     * Возвращает DAO для работы с балансом пользователя для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить DAO
     * @return InMemoryBalanceDao, если DataType пользователя равен IN_MEMORY, иначе GoogleSheetsBalanceDao.
     */
    public BalanceDao getBalanceDaoForUser(long chatId) {
        return inMemoryDaoGroup.getInMemoryUserDao().getDataType(chatId).equals(DataType.IN_MEMORY)
                ? inMemoryDaoGroup.getInMemoryBalanceDao()
                : googleSheetsDaoGroup.getGoogleSheetsBalanceDao();

    }

    /**
     * Возвращает DAO для работы с доходами пользователя для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить DAO
     * @return InMemoryIncomeDao, если DataType пользователя равен IN_MEMORY, иначе GoogleSheetsIncomeDao.
     */
    public IncomeDao getIncomeDaoForUser(long chatId) {
        return inMemoryDaoGroup.getInMemoryUserDao().getDataType(chatId).equals(DataType.IN_MEMORY)
                ? inMemoryDaoGroup.getInMemoryIncomeDao()
                : googleSheetsDaoGroup.getGoogleSheetsIncomeDao();
    }

    /**
     * Возвращает DAO для работы с расходами пользователя для указанного идентификатора чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить DAO
     * @return InMemoryExpenseDao, если DataType пользователя равен IN_MEMORY, иначе GoogleSheetsExpenseDao.
     */
    public ExpenseDao getExpenseDaoForUser(long chatId) {
        return inMemoryDaoGroup.getInMemoryUserDao().getDataType(chatId).equals(DataType.IN_MEMORY)
                ? inMemoryDaoGroup.getInMemoryExpenseDao()
                : googleSheetsDaoGroup.getGoogleSheetsExpenseDao();
    }
}
