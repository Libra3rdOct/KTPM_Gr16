package pf.Chart;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap; // Import TreeMap to sort the data
import pf.Database.DatabaseManager;
import pf.Database.UserSession;
import javax.swing.JFrame;
import org.knowm.xchart.SwingWrapper;
// 1) import mới
import java.util.LinkedHashMap;     // thêm dòng này

public class IncomeExpenseChart {

    // Array of month names in chronological order
    private static final String[] MONTHS = {
        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October",
        "November", "December"
    };

    public static Map<String, Integer> getMonthlyIncome() throws SQLException {
        Map<String, Integer> monthlyIncome = new LinkedHashMap<>();

        DatabaseManager.connect();
        String query
                = "SELECT MONTH(income_date)          AS m_num, "
                + "       DATE_FORMAT(income_date,'%M') AS m_name, "
                + "       SUM(amount)                  AS income "
                + "FROM   income "
                + "WHERE  user_id = ? "
                + "GROUP  BY m_num, m_name "
                + "ORDER  BY m_num";

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(query)) {
            ps.setInt(1, UserSession.userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String month = rs.getString("m_name").trim(); // ví dụ "January"
                int inc = rs.getInt("income");
                monthlyIncome.put(month, inc);
            }
        }

        // bảo đảm đủ 12 tháng
        for (String m : MONTHS) {
            monthlyIncome.putIfAbsent(m, 0);
        }
        return monthlyIncome;
    }

    public static Map<String, Integer> getMonthlyExpenses() throws SQLException {
        Map<String, Integer> monthlyExpenses = new LinkedHashMap<>();

        DatabaseManager.connect();
        String query
                = "SELECT MONTH(expense_date)            AS m_num, "
                + "       DATE_FORMAT(expense_date,'%M') AS m_name, "
                + "       SUM(amount)                    AS expense "
                + "FROM   expense "
                + "WHERE  user_id = ? "
                + "GROUP  BY m_num, m_name "
                + "ORDER  BY m_num";

        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(query)) {
            ps.setInt(1, UserSession.userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String month = rs.getString("m_name").trim();
                int exp = rs.getInt("expense");
                monthlyExpenses.put(month, exp);
            }
        }

        for (String m : MONTHS) {
            monthlyExpenses.putIfAbsent(m, 0);
        }
        return monthlyExpenses;
    }

    public static void generateChart(Map<String, Integer> monthlyIncome, Map<String, Integer> monthlyExpenses) {

        Thread chartThread = new Thread(() -> {
            // Create XChart
            CategoryChart chart = new CategoryChartBuilder()
                    .width(1000)
                    .height(504)
                    .title("Thu nhập hàng tháng so với chi phí")
                    .xAxisTitle("Tháng")
                    .yAxisTitle("Số tiền")
                    .build();

            // Add income and expense series to the chart
            chart.addSeries("Thu nhập", new ArrayList<>(monthlyIncome.keySet()), new ArrayList<>(monthlyIncome.values()));
            chart.addSeries("Chi phí", new ArrayList<>(monthlyExpenses.keySet()),
                    new ArrayList<>(monthlyExpenses.values()));

            SwingWrapper<CategoryChart> wrapper = new SwingWrapper<>(chart);
            JFrame frame = wrapper.displayChart();

            // Override the default close operation of the JFrame
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        });
        // Start the thread
        chartThread.start();
    }
}
