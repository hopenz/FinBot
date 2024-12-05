package ru.naumen.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.naumen.bot.data.dao.BalanceDao;
import ru.naumen.bot.data.dao.ExpenseDao;
import ru.naumen.bot.data.dao.LimitDao;
import ru.naumen.bot.data.dao.provider.BalanceDaoProvider;
import ru.naumen.bot.data.dao.provider.ExpenseDaoProvider;
import ru.naumen.bot.data.dao.provider.LimitDaoProvider;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.ExpenseCategory;
import ru.naumen.bot.data.entity.Limit;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.exception.ExceedingTheLimitException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Сервис IncomeService предоставляет методы для работы с расходами пользователя.
 */
@Service
public class ExpenseService {

    /**
     * Провайдер DAO для работы с расходами пользователей.
     */
    private final ExpenseDaoProvider expenseDaoProvider;

    /**
     * Провайдер DAO для работы с лимитами расходов пользователей.
     */
    private final LimitDaoProvider limitDaoProvider;

    /**
     * Провайдер DAO для работы с балансом пользователей.
     */
    private final BalanceDaoProvider balanceDaoProvider;

    /**
     * Сервис для взаимодействия с данными пользователя.
     */
    private final UserService userService;

    /**
     * Логгер для записи сообщений об ошибках
     */
    private final Logger logger = LoggerFactory.getLogger(ExpenseService.class);

    /**
     * Конструктор ExpenseService
     *
     * @param expenseDaoProvider Провайдер DAO для работы с расходами пользователей
     * @param limitDaoProvider   Провайдер DAO для работы с лимитами расходов пользователей
     * @param balanceDaoProvider Провайдер DAO для работы с балансом пользователей
     * @param userService        Сервис для взаимодействия с данными пользователя
     */
    public ExpenseService(ExpenseDaoProvider expenseDaoProvider,
                          LimitDaoProvider limitDaoProvider, BalanceDaoProvider balanceDaoProvider,
                          UserService userService) {
        this.expenseDaoProvider = expenseDaoProvider;
        this.limitDaoProvider = limitDaoProvider;
        this.balanceDaoProvider = balanceDaoProvider;
        this.userService = userService;
    }

    /**
     * Возвращает список расходов пользователя на основе chatId пользователя.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     * @return список объектов {@link Expense}, представляющих расходы пользователя.
     */
    public List<Expense> getExpenses(long chatId) throws DaoException {
        ExpenseDao expenseDao = expenseDaoProvider.getExpenseDaoForUser(chatId);
        return expenseDao.getExpenses(chatId);
    }

    /**
     * Добавляет расход в хранилище, обновляет баланс и сумму расходов за день.
     * По умолчанию установлена категория расхода "Другое".
     *
     * @param expense сообщение от пользователя.
     * @param chatId  идентификатор чата, в котором было отправлено сообщение
     */
    public void addExpense(String expense, long chatId) throws DaoException, ExceedingTheLimitException {
        ExpenseDao expenseDao = expenseDaoProvider.getExpenseDaoForUser(chatId);
        BalanceDao balanceDao = balanceDaoProvider.getBalanceDaoForUser(chatId);
        LimitDao limitDao = limitDaoProvider.getLimitDaoForUser(chatId);

        String[] arrayOfStringExpense = expense.split(" ", 3);
        Expense newExpense = new Expense(
                arrayOfStringExpense[2], Double.parseDouble(arrayOfStringExpense[1]),
                ExpenseCategory.OTHER, LocalDate.now());
        expenseDao.addExpense(chatId, newExpense);
        balanceDao.setBalance(chatId,
                balanceDao.getBalance(chatId) - Double.parseDouble(arrayOfStringExpense[1]));

        if (limitDao.getLimit(chatId) != null) {
            Limit limit = limitDao.getLimit(chatId);
            try {
                limit.setDailyExpensesSum(limit.getDailyExpensesSum() + Double.parseDouble(arrayOfStringExpense[1]));
            } finally {
                limitDao.setLimit(chatId, limit);
            }
        }
    }

    /**
     * Добавляет список расходов в хранилище и обновляет баланс.
     *
     * @param chatId   идентификатор чата, в котором было отправлено сообщение
     * @param expenses список объектов {@link Expense}, представляющих расходы пользователя.
     */
    public void addExpenses(long chatId, List<Expense> expenses) throws DaoException {
        ExpenseDao expenseDao = expenseDaoProvider.getExpenseDaoForUser(chatId);
        expenseDao.addExpenses(chatId, expenses);
    }

    /**
     * Удаляет расходы из хранилища.
     *
     * @param chatId идентификатор чата, в котором было отправлено сообщение
     */
    public void removeExpenses(long chatId) throws DaoException {
        ExpenseDao expenseDao = expenseDaoProvider.getExpenseDaoForUser(chatId);
        expenseDao.removeExpenses(chatId);
    }

    /**
     * Изменяет категорию последнего расхода для указанного чата.
     *
     * @param chatId   идентификатор чата пользователя, для которого требуется изменить категорию расхода.
     * @param category строковое значение, представляющее имя категории расхода, на которую нужно изменить.
     */
    public void changeLastExpenseCategory(long chatId, String category) throws DaoException {
        ExpenseDao expenseDao = expenseDaoProvider.getExpenseDaoForUser(chatId);
        ExpenseCategory newCategory = Arrays.stream(ExpenseCategory.values())
                .filter(expenseCategory -> expenseCategory.getName().equalsIgnoreCase(category))
                .findFirst()
                .orElse(ExpenseCategory.OTHER);
        expenseDao.changeLastExpenseCategory(chatId, newCategory);
    }

    /**
     * Устанавливает лимит расходов для указанного чата.
     *
     * @param chatId  идентификатор чата, для которого необходимо установить лимит.
     * @param message сообщение от пользователя с новым лимитом расходов.
     */
    public void setExpensesLimit(long chatId, String message) throws DaoException {
        LimitDao limitDao = limitDaoProvider.getLimitDaoForUser(chatId);
        double limit = Double.parseDouble(message);
        if (limit <= 0.0) {
            throw new IllegalArgumentException("Лимит не может быть отрицательным или нулём");
        }
        limitDao.setLimit(chatId, new Limit(limit, 0.0));
    }

    /**
     * Возвращает текущий лимит расходов для указанного чата.
     *
     * @param chatId идентификатор чата, для которого необходимо получить лимит расходов.
     * @return объект {@link Limit}, представляющий текущий лимит расходов пользователя.
     */
    public Limit getExpensesLimit(long chatId) throws DaoException {
        return limitDaoProvider.getLimitDaoForUser(chatId).getLimit(chatId);
    }

    /**
     * Удаляет установленный лимит расходов для указанного чата.
     *
     * @param chatId идентификатор чата, для которого необходимо удалить лимит расходов.
     */
    public void removeExpensesLimit(long chatId) throws DaoException {
        limitDaoProvider.getLimitDaoForUser(chatId).removeLimit(chatId);
    }

    /**
     * Устанавливает новый лимит расходов для указанного чата.
     *
     * @param chatId идентификатор чата, для которого необходимо установить новый лимит расходов.
     * @param limit  объект {@link Limit}, представляющий новый лимит расходов пользователя.
     */
    public void setExpensesLimit(long chatId, Limit limit) throws DaoException {
        limitDaoProvider.getLimitDaoForUser(chatId).setLimit(chatId, limit);
    }

    /**
     * Обнуляет сумму расходов за день в 00:00.
     */
    @Scheduled(cron = "${scheduling.time-to-reset-limits}")
    private void resetLimits() {
        Set<Long> usersId = userService.getUsers();
        for (Long userId : usersId) {
            LimitDao limitDao = limitDaoProvider.getLimitDaoForUser(userId);
            try {
                Limit limit = limitDao.getLimit(userId);
                try {
                    limit.setDailyExpensesSum(0.0);
                } finally {
                    limitDao.setLimit(userId, limit);
                }
            } catch (DaoException e) {
                logger.error("[Dao exception] :: Message: {}", e.getMessage(), e);
            } catch (ExceedingTheLimitException ignored) {
            }
        }
    }
}
