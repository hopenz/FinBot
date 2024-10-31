package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Update;
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
     * Получает список доходов пользователя на основе информации из обновления Telegram.
     *
     * @param update обновление от Telegram.
     * @return список объектов {@link Income}, представляющих доходы пользователя.
     */
    public List<Income> getIncomes(Update update) {
        return incomeDao.getIncomes(update.message().chat().id());
    }

    /**
     * Добавляет доходы в хранилище и обнавляет баланс.
     *
     * @param income сообщение от пользователя.
     * @param update обновление от Telegrem.
     */
    public void addIncome(String income, Update update) {
        String[] arrayOfStringIncome = income.split(" ", 3);
        long chatId = update.message().chat().id();
        Income newIncome = new Income(
                arrayOfStringIncome[2], Double.parseDouble(arrayOfStringIncome[1]), LocalDate.now());
        incomeDao.addIncome(chatId, newIncome);
        balanceDao.setBalance(chatId,
                balanceDao.getBalance(chatId) + Double.parseDouble(arrayOfStringIncome[1]));
    }
}
