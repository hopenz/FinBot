package ru.naumen.bot.data.entity;

import ru.naumen.bot.exception.ExceedingTheLimitException;

/**
 * Класс, представляющий лимит расходов для пользователя.
 */
public class Limit {

    /**
     * Ежедневный лимит расходов.
     */
    private final Double dailyLimit;

    /**
     * Текущая сумма ежедневных расходов.
     */
    private Double dailyExpensesSum;

    /**
     * Конструктор класса {@link Limit}.
     *
     * @param dailyLimit       Ежедневный лимит расходов.
     * @param dailyExpensesSum Текущая сумма ежедневных расходов.
     */
    public Limit(Double dailyLimit, Double dailyExpensesSum) {
        this.dailyLimit = dailyLimit;
        this.dailyExpensesSum = dailyExpensesSum;
    }

    /**
     * Возвращает значение ежедневного лимита расходов.
     */
    public Double getDailyLimit() {
        return dailyLimit;
    }

    /**
     * Возвращает текущую сумму ежедневных расходов.
     */
    public Double getDailyExpensesSum() {
        return dailyExpensesSum;
    }

    /**
     * Устанавливает новую сумму ежедневных расходов и проверяет, не превышает ли она лимит.
     *
     * @param dailyExpensesSum Новая сумма ежедневных расходов.
     * @throws ExceedingTheLimitException Если новая сумма превышает лимит.
     */
    public void setDailyExpensesSum(Double dailyExpensesSum) throws ExceedingTheLimitException {
        this.dailyExpensesSum = dailyExpensesSum;
        if (dailyExpensesSum > dailyLimit) {
            throw new ExceedingTheLimitException(dailyExpensesSum);
        }
    }
}
