package ru.naumen.bot.telegramBot.filter;

import com.pengrad.telegrambot.model.Update;

/**
 * Абстрактный класс ABotFilter представляет собой элемент цепочки фильтров для обработки обновлений Telegram.
 * Каждый фильтр может передавать обработку следующему фильтру в цепочке, если он не завершил обработку сам.
 */
public abstract class ABotFilter {

    /**
     * Следующий фильтр в цепочке.
     */
    private ABotFilter nextFilter;

    /**
     * Конструктор без параметров.
     * Инициализирует фильтр без следующего фильтра.
     */
    public ABotFilter() {
    }

    /**
     * Конструктор с параметром.
     * Инициализирует фильтр и задает следующий фильтр в цепочке.
     *
     * @param nextFilter следующий фильтр в цепочке.
     */
    public ABotFilter(ABotFilter nextFilter) {
        this.nextFilter = nextFilter;
    }

    /**
     * Выполняет фильтрацию обновления.
     * Если текущий фильтр не завершает обработку, обновление передается следующему фильтру в цепочке.
     *
     * @param update обновление от Telegram, которое нужно обработать.
     */
    public void doFilter(Update update) {
        if (nextFilter != null) {
            nextFilter.doFilter(update);
        }
    }

    /**
     * Устанавливает следующий фильтр в цепочке.
     *
     * @param nextFilter следующий фильтр, который будет вызван после текущего.
     */
    public void setNextFilter(ABotFilter nextFilter) {
        this.nextFilter = nextFilter;
    }

    /**
     * Возвращает следующий фильтр в цепочке.
     *
     * @return следующий фильтр.
     */
    public ABotFilter getNextFilter() {
        return nextFilter;
    }
}
