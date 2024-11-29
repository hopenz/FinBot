package ru.naumen.bot.data.dao.googleSheets;

import org.springframework.stereotype.Component;

/**
 * Компонент, объединяющий DAO для операций с Google Sheets.
 */
@Component
public class GoogleSheetsDaoGroup {

    /**
     * DAO для управления балансом пользователя в гугл таблице
     */
    private final GoogleSheetsIncomeDao googleSheetsIncomeDao;

    /**
     * DAO для управления расходами пользователя в гугл таблице
     */
    private final GoogleSheetsExpenseDao googleSheetsExpenseDao;

    /**
     * DAO для управления балансом пользователя в гугл таблице
     */
    private final GoogleSheetsBalanceDao googleSheetsBalanceDao;

    /**
     * Конструктор для инициализации всех зависимостей DAO.
     *
     * @param googleSheetsIncomeDao  DAO для управления доходами в гугл таблице
     * @param googleSheetsExpenseDao DAO для управления расходами в гугл таблице
     * @param googleSheetsBalanceDao DAO для управления балансом в гугл таблице
     */
    public GoogleSheetsDaoGroup(GoogleSheetsIncomeDao googleSheetsIncomeDao,
                                GoogleSheetsExpenseDao googleSheetsExpenseDao,
                                GoogleSheetsBalanceDao googleSheetsBalanceDao) {
        this.googleSheetsIncomeDao = googleSheetsIncomeDao;
        this.googleSheetsExpenseDao = googleSheetsExpenseDao;
        this.googleSheetsBalanceDao = googleSheetsBalanceDao;
    }

    /**
     * Возвращает экземпляр GoogleSheetsIncomeDao.
     */
    public GoogleSheetsIncomeDao getGoogleSheetsIncomeDao() {
        return googleSheetsIncomeDao;
    }

    /**
     * Возвращает экземпляр GoogleSheetsExpenseDao.
     */
    public GoogleSheetsExpenseDao getGoogleSheetsExpenseDao() {
        return googleSheetsExpenseDao;
    }

    /**
     * Возвращает экземпляр GoogleSheetsBalanceDao.
     */
    public GoogleSheetsBalanceDao getGoogleSheetsBalanceDao() {
        return googleSheetsBalanceDao;
    }
}
