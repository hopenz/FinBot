package ru.naumen.bot.data.entity;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.naumen.bot.exception.ExceedingTheLimitException;

/**
 * Тесты для класса {@link Limit}, проверяющие корректность обработки лимита.
 */
public class LimitTest {

    /**
     * Объект тестируемого класса
     */
    private Limit limit;

    /**
     * Установление лимита перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        limit = new Limit(100.0, 0.0);
    }

    /**
     * Тестирование, когда сумма расходов превышает лимит
     */
    @Test
    void testWhenSumExpensesMoreThanLimit() {
        Assertions.assertThatThrownBy(() ->
                limit.setDailyExpensesSum(101.0)).isInstanceOf(ExceedingTheLimitException.class);
    }

    /**
     * Тестирование, когда сумма расходов не превышает лимит
     *
     * @throws ExceedingTheLimitException Исключение, которое выбрасывается,
     *                                    когда сумма ежедневных расходов превышает установленный лимит
     */
    @Test
    void testWhenSumExpensesLessThanLimit() throws ExceedingTheLimitException {
        limit.setDailyExpensesSum(99.0);
        Assertions.assertThat(limit.getDailyExpensesSum()).isEqualTo(99.0);
    }
}