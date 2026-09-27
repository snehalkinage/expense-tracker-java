import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manages per-category monthly budget limits, persisted to a simple CSV file.
 * Each line: category,limitAmount
 */
public class BudgetManager {
    private final Path filePath;
    private final Map<String, Double> budgets;

    public BudgetManager(String fileName) {
        this.filePath = Paths.get(fileName);
        this.budgets = load();
    }

    /** Sets (or updates) the monthly budget limit for a category. */
    public void setBudget(String category, double limit) {
        budgets.put(category, limit);
        save();
    }

    /** Removes the budget limit for a category, if one exists. */
    public boolean removeBudget(String category) {
        boolean removed = budgets.remove(category) != null;
        if (removed) save();
        return removed;
    }

    /** Returns the budget limit for a category, or empty if none is set. */
    public Optional<Double> getBudget(String category) {
        return Optional.ofNullable(budgets.get(category));
    }

    /** Returns all budgets as an unmodifiable, category-sorted map. */
    public Map<String, Double> getAllBudgets() {
        return new TreeMap<>(budgets);
    }

    private Map<String, Double> load() {
        Map<String, Double> result = new HashMap<>();
        if (!Files.exists(filePath)) {
            return result;
        }
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    try {
                        result.put(parts[0], Double.parseDouble(parts[1]));
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping malformed budget line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading budgets file: " + e.getMessage());
        }
        return result;
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Map.Entry<String, Double> entry : budgets.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving budgets file: " + e.getMessage());
        }
    }
}