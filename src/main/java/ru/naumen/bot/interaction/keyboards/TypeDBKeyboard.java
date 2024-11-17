package ru.naumen.bot.interaction.keyboards;

/**
 * Перечисление типов баз данных для кнопок инлайн клавиатуры.
 */
public enum TypeDBKeyboard {

    /**
     * Хранение в оперативной памяти
     */
    IN_MEMORY("Оперативная память"),

    /**
     * Хранение в Google Sheets
     */
    GOOGLE_SHEETS("Гугл-таблица");

    /**
     * Значение кнопки
     */
    private final String data;

    /**
     * Конструктор
     * @param data значение кнопки
     */
    TypeDBKeyboard(String data) {
        this.data = data;
    }

    /**
     * Возвращение значения кнопки
     * @return значение кнопки
     */
    public String getData() {
        return data;
    }
}
