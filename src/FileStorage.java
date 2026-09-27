import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles loading and saving expenses to a CSV file on disk.
 */
public class FileStorage implements ExpenseStorage {
    private final Path filePath;

    public FileStorage(String fileName) {
        this.filePath = Paths.get(fileName);
    }

    /** Loads all expenses from the CSV file. Returns an empty list if the file doesn't exist yet. */
    public List<Expense> load() {
        List<Expense> expenses = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return expenses;
        }
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    expenses.add(Expense.fromCsvLine(line));
                } catch (Exception e) {
                    System.out.println("Skipping malformed line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return expenses;
    }

    /** Overwrites the CSV file with the given list of expenses. */
    public void save(List<Expense> expenses) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (Expense e : expenses) {
                writer.write(e.toCsvLine());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}