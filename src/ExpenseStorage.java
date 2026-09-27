import java.util.List;

/**
 * Common contract for anything that can load and save a list of expenses.
 * Implemented by FileStorage (CSV) and SQLiteStorage (embedded database),
 * so ExpenseManager can work with either without knowing which one it has.
 */
public interface ExpenseStorage {
    List<Expense> load();
    void save(List<Expense> expenses);
}