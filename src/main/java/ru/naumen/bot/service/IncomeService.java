package ru.naumen.bot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.dao.provider.BalanceDaoProvider;
import ru.naumen.bot.data.dao.provider.IncomeDaoProvider;
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
     * Провайдер DAO для работы с доходами пользователей.
     */
    private final IncomeDaoProvider incomeDaoProvider;

    /**
     * Провайдер DAO для работы с балансом пользователей.
     */
    private final BalanceDaoProvider balanceDaoProvider;

    /**
     * Конструктор класса IncomeService.
     *
     * @param IncomeDaoProvider  Провайдер DAO для работы с доходами пользователей
     * @param balanceDaoProvider Провайдер DAO для работы с балансом пользователей
     */
    public IncomeService(IncomeDaoProvider IncomeDaoProvider, BalanceDaoProvider balanceDaoProvider) {
        this.incomeDaoProvider = IncomeDaoProvider;
        this.balanceDaoProvider = balanceDaoProvider;
    }

    /**
     * Возвращает список доходов пользователя на основе chatId пользователя.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     * @return список объектов {@link Income}, представляющих доходы пользователя.
     */
    public List<Income> getIncomes(long chatId) throws DaoException {
        IncomeDao incomeDao = incomeDaoProvider.getIncomeDaoForUser(chatId);
        return incomeDao.getIncomes(chatId);
    }

    /**
     * Добавляет доход в хранилище и обновляет баланс.
     *
     * @param income сообщение от пользователя.
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     */
    public void addIncome(String income, long chatId) throws DaoException {
        IncomeDao incomeDao = incomeDaoProvider.getIncomeDaoForUser(chatId);
        BalanceDao balanceDao = balanceDaoProvider.getBalanceDaoForUser(chatId);
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
        IncomeDao incomeDao = incomeDaoProvider.getIncomeDaoForUser(chatId);
        incomeDao.addIncomes(chatId, incomes);
    }

    /**
     * Удаляет доходы из хранилища.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     */
    public void removeIncomes(long chatId) throws DaoException {
        IncomeDao incomeDao = incomeDaoProvider.getIncomeDaoForUser(chatId);
        incomeDao.removeIncomes(chatId);
    }
}
