import java.time.LocalDate;
import java.util.*;

/**
 * Manages the in-memory collection of expenses: add, edit, delete, list.
 * Delegates persistence to any ExpenseStorage implementation (CSV file or SQLite database).
 */
public class ExpenseManager {
    private final List<Expense> expenses;
    private final ExpenseStorage storage;
    private int nextId;

    public ExpenseManager(ExpenseStorage storage) {
        this.storage = storage;
        this.expenses = storage.load();
        this.nextId = expenses.stream().mapToInt(Expense::getId).max().orElse(0) + 1;
    }

    public Expense addExpense(LocalDate date, double amount, String category, String note) {
        Expense e = new Expense(nextId++, date, amount, category, note);
        expenses.add(e);
        persist();
        return e;
    }

    public boolean deleteExpense(int id) {
        boolean removed = expenses.removeIf(e -> e.getId() == id);
        if (removed) persist();
        return removed;
    }

    public Optional<Expense> findById(int id) {
        return expenses.stream().filter(e -> e.getId() == id).findFirst();
    }

    public boolean editExpense(int id, LocalDate newDate, Double newAmount, String newCategory, String newNote) {
        Optional<Expense> found = findById(id);
        if (found.isEmpty()) return false;
        Expense e = found.get();
        if (newDate != null) e.setDate(newDate);
        if (newAmount != null) e.setAmount(newAmount);
        if (newCategory != null && !newCategory.isBlank()) e.setCategory(newCategory);
        if (newNote != null) e.setNote(newNote);
        persist();
        return true;
    }

    public List<Expense> getAllSortedByDate() {
        List<Expense> copy = new ArrayList<>(expenses);
        copy.sort(Comparator.comparing(Expense::getDate));
        return copy;
    }

    public List<Expense> getForMonth(int month, int year) {
        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses) {
            if (e.getDate().getMonthValue() == month && e.getDate().getYear() == year) {
                result.add(e);
            }
        }
        result.sort(Comparator.comparing(Expense::getDate));
        return result;
    }

    public List<Expense> getAll() {
        return new ArrayList<>(expenses);
    }

    private void persist() {
        storage.save(expenses);
    }
}