package ru.naumen.bot.interaction.keyboards;

import org.springframework.stereotype.Component;
import ru.naumen.bot.data.enums.ExpenseCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс для формирования клавиатуры с кнопками категорий расходов.
 */
@Component
public class CategoriesKeyboard {

    /**
     * Формирует список групп кнопок для категорий расходов.
     *
     * @param countCategoriesInGroup количество кнопок в одной строке клавиатуры
     * @return список строк кнопок, где каждая строка содержит не более заданного количества категорий
     */
    public List<List<String>> getCategoriesInGroups(int countCategoriesInGroup) {
        List<String> categories = Arrays.stream(ExpenseCategory.values()).
                map(ExpenseCategory::getName).
                toList();
        List<List<String>> categoriesKeyboard = new ArrayList<>();
        for (int i = 0; i < categories.size(); i += countCategoriesInGroup) {
            categoriesKeyboard.add(categories.subList(i, Math.min(i + countCategoriesInGroup, categories.size())));
        }
        return categoriesKeyboard;
    }
}
