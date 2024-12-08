package ru.naumen.bot.exception;

/**
 * Исключение, которое выбрасывается, когда сумма ежедневных расходов превышает установленный лимит
 */
public class ExceedingTheLimitException extends Exception {

    /**
     * Общая сумма ежедневных расходов.
     */
    private final Double dailyExpensesSum;

    /**
     * Конструктор для создания исключения с указанием лимита и суммы расходов.
     *
     * @param dailyExpensesSum Текущая сумма ежедневных расходов.
     */
    public ExceedingTheLimitException(Double dailyExpensesSum) {
        this.dailyExpensesSum = dailyExpensesSum;
    }

    /**
     * Возвращает текущую сумму ежедневных расходов.
     */
    public Double getDailyExpensesSum() {
        return dailyExpensesSum;
    }
}
