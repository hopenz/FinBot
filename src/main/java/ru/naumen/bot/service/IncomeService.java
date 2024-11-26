package ru.naumen.bot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.DaoProvider;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.exception.DaoException;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис IncomeService предоставляет методы для работы с доходами пользователя.
 */
@Service
public class IncomeService {

    /**
     * Класс, предоставляющий доступ к DAO-объектам для работы с данными пользователей.
     */
    private final DaoProvider daoProvider;

    /**
     * Конструктор класса IncomeService.
     *
     * @param daoProvider объект, предоставляющий доступ к DAO-объектам для работы с данными пользователей.
     */
    public IncomeService(DaoProvider daoProvider) {
        this.daoProvider = daoProvider;
    }

    /**
     * Получает список доходов пользователя на основе chatId пользователя.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     * @return список объектов {@link Income}, представляющих доходы пользователя.
     */
    public List<Income> getIncomes(long chatId) throws DaoException {
        IncomeDao incomeDao = daoProvider.getIncomeDaoForUser(chatId);
        return incomeDao.getIncomes(chatId);
    }

    /**
     * Добавляет доход в хранилище и обновляет баланс.
     *
     * @param income сообщение от пользователя.
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     */
    public void addIncome(String income, long chatId) throws DaoException {
        IncomeDao incomeDao = daoProvider.getIncomeDaoForUser(chatId);
        BalanceDao balanceDao = daoProvider.getBalanceDaoForUser(chatId);
        String[] arrayOfStringIncome = income.split(" ", 3);
        Income newIncome = new Income(
                arrayOfStringIncome[2], Double.parseDouble(arrayOfStringIncome[1]), LocalDate.now());
        incomeDao.addIncome(chatId, newIncome);
        balanceDao.setBalance(chatId,
                balanceDao.getBalance(chatId) + Double.parseDouble(arrayOfStringIncome[1]));
    }

    /**
     * Добавляет список доходов в хранилище и обновляет баланс.
     *
     * @param chatId  идентификатор чата, в котором было отправлено сообщение
     * @param incomes список объектов {@link Income}, представляющих расходы пользователя.
     */
    public void addIncomes(long chatId, List<Income> incomes) throws DaoException {
        IncomeDao incomeDao = daoProvider.getIncomeDaoForUser(chatId);
        incomeDao.addIncomes(chatId, incomes);
    }

    /**
     * Удаляет доходы из хранилища.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     */
    public void removeIncomes(long chatId) throws DaoException {
        IncomeDao incomeDao = daoProvider.getIncomeDaoForUser(chatId);
        incomeDao.removeIncomes(chatId);
    }
}
