package ru.naumen.bot.telegramBot.service;

import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.IncomeDao;
import ru.naumen.bot.data.entity.Income;

import java.time.LocalDate;
import java.util.List;

/**
 * Сервис IncomeService предоставляет методы для работы с доходами пользователя.
 */
@Service
public class IncomeService {

    /**
     * DAO для работы с данными о доходах.
     */
    private final IncomeDao incomeDao;

    /**
     * Dao для работы с балансом.
     */
    private final BalanceDao balanceDao;


    /**
     * Конструктор IncomeService. Инициализирует сервис с объектами DAO.
     *
     * @param incomeDao  DAO для работы с доходами.
     * @param balanceDao DAO для работы с балансом.
     */
    public IncomeService(IncomeDao incomeDao, BalanceDao balanceDao) {
        this.incomeDao = incomeDao;
        this.balanceDao = balanceDao;
    }

    /**
     * Получает список доходов пользователя на основе chatId пользователя.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     * @return список объектов {@link Income}, представляющих доходы пользователя.
     */
    public List<Income> getIncomes(long chatId) {
        return incomeDao.getIncomes(chatId);
    }

    /**
     * Добавляет доходы в хранилище и обнавляет баланс.
     *
     * @param income сообщение от пользователя.
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     */
    public void addIncome(String income, long chatId) {
        String[] arrayOfStringIncome = income.split(" ", 3);
        Income newIncome = new Income(
                arrayOfStringIncome[2], Double.parseDouble(arrayOfStringIncome[1]), LocalDate.now());
        incomeDao.addIncome(chatId, newIncome);
        balanceDao.setBalance(chatId,
                balanceDao.getBalance(chatId) + Double.parseDouble(arrayOfStringIncome[1]));
    }
}
