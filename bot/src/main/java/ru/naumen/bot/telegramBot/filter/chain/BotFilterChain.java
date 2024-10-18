package ru.naumen.bot.telegramBot.filter.chain;

import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.naumen.bot.telegramBot.filter.ABotFilter;

import java.util.List;

/**
 * Класс BotFilterChain отвечает за создание и управление цепочкой фильтров для обработки обновлений Telegram.
 * Цепочка фильтров выполняется последовательно, каждый фильтр передает управление следующему.
 */
@Component
public class BotFilterChain {

    /**
     * Список фильтров, которые будут применяться последовательно.
     */
    private final List<ABotFilter> filters;

    /**
     * Конструктор класса BotFilterChain.
     *
     * @param filters список фильтров, который используется для построения цепочки.
     */
    public BotFilterChain(List<ABotFilter> filters) {
        this.filters = filters;
    }

    /**
     * Метод вызывается после создания компонента.
     * Конфигурирует цепочку фильтров, связывая их последовательно.
     * Каждый фильтр будет вызывать следующий.
     */
    @PostConstruct
    private void configureFilterChain() {
        for (int i = 0; i < filters.size() - 1; i++) {
            filters.get(i).setNextFilter(filters.get(i + 1));
        }
    }

    /**
     * Запускает процесс обработки обновления.
     * Начинает обработку с первого фильтра в цепочке.
     *
     * @param update обновление от Telegram, которое нужно обработать.
     */
    public void process(Update update) {
        if (!filters.isEmpty()) {
            filters.getFirst().doFilter(update);
        }
    }

}
