import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading and saving expenses using an embedded SQLite database
 * instead of a flat CSV file. Drop-in alternative to FileStorage —
 * exposes the same load()/save(List) methods so ExpenseManager doesn't
 * need to change at all.
 */
public class SQLiteStorage implements ExpenseStorage {
    private final String url;

    public SQLiteStorage(String dbFileName) {
        this.url = "jdbc:sqlite:" + dbFileName;
        initSchema();
    }

    private void initSchema() {
        String sql = """
            CREATE TABLE IF NOT EXISTS expenses (
                id INTEGER PRIMARY KEY,
                date TEXT NOT NULL,
                amount REAL NOT NULL,
                category TEXT NOT NULL,
                note TEXT
            )
            """;
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    /** Loads all expenses from the database. */
    public List<Expense> load() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, date, amount, category, note FROM expenses ORDER BY id";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDate date = LocalDate.parse(rs.getString("date"));
                double amount = rs.getDouble("amount");
                String category = rs.getString("category");
                String note = rs.getString("note");
                expenses.add(new Expense(id, date, amount, category, note));
            }
        } catch (SQLException e) {
            System.out.println("Error loading expenses: " + e.getMessage());
        }
        return expenses;
    }

    /**
     * Overwrites the table with the given list of expenses.
     * Simplest correct approach: clear the table, then re-insert everything.
     * Fine for a mini-project's scale (hundreds to low thousands of rows).
     */
    public void save(List<Expense> expenses) {
        String deleteSql = "DELETE FROM expenses";
        String insertSql = "INSERT INTO expenses (id, date, amount, category, note) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(deleteSql);
            }
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Expense e : expenses) {
                    ps.setInt(1, e.getId());
                    ps.setString(2, e.getDate().toString());
                    ps.setDouble(3, e.getAmount());
                    ps.setString(4, e.getCategory());
                    ps.setString(5, e.getNote());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }
}