package ru.naumen.bot.data.enums;

/**
 * Перечисление, представляющее категории расходов.
 */
public enum ExpenseCategory {

    /**
     * Категория расходов для переводов.
     */
    TRANSFER("Переводы"),

    /**
     * Категория расходов для транспорта.
     */
    TRANSPORT("Транспорт"),

    /**
     * Категория расходов для супермаркета.
     */
    SUPERMARKET("Супермаркет"),

    /**
     * Категория расходов для маркетплейсов.
     */
    MARKETPLACE("Маркетплейсы"),

    /**
     * Категория расходов для кафе и ресторанов.
     */
    RESTAURANT("Рестораны"),

    /**
     * Категория расходов для одежды.
     */
    CLOTHING("Одежда"),

    /**
     * Категория расходов для хобби и развлечений.
     */
    ENTERTAINMENT("Развлечения"),

    /**
     * Категория расходов для здоровья.
     */
    HEALTH("Здоровье"),

    /**
     * Категория для расходов, не подходящих под имеющиеся категории.
     */
    OTHER("Другое");

    /**
     * Имя категории расхода
     */
    private final String name;

    /**
     * Конструктор для инициализации категории с ее именем.
     *
     * @param name имя категории расхода.
     */
    ExpenseCategory(String name) {
        this.name = name;
    }

    /**
     * Возвращает имя категории.
     */
    public String getName() {
        return name;
    }

}
