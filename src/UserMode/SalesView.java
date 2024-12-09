package UserMode;

import Database.DatabaseHandler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SalesView {
    private JFrame frame;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel; // Add this field

    public void showSalesView() {
        frame = new JFrame("Sales View");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        // Add back button
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.dispose());
        headerPanel.add(backBtn, BorderLayout.WEST);
        
        // Brand buttons panel
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] brands = DatabaseHandler.getBrands();
        
        // Add "All Brands" button
        JButton allBrandsBtn = new JButton("All Brands");
        allBrandsBtn.addActionListener(e -> updateTable(null));
        brandPanel.add(allBrandsBtn);
        
        // Add button for each brand
        for (String brand : brands) {
            JButton brandBtn = new JButton(brand);
            brandBtn.addActionListener(e -> updateTable(brand));
            brandPanel.add(brandBtn);
        }

        // Add sorting panel
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton sortByDateBtn = new JButton("Sort by Date");
        JButton sortByTotalBtn = new JButton("Sort by Total");
        JButton sortByQuantityBtn = new JButton("Sort by Quantity");

        sortByDateBtn.addActionListener(e -> sortTable(4)); // Date column
        sortByTotalBtn.addActionListener(e -> sortTable(3)); // Total column
        sortByQuantityBtn.addActionListener(e -> sortTable(2)); // Quantity column

        sortPanel.add(sortByDateBtn);
        sortPanel.add(sortByTotalBtn);
        sortPanel.add(sortByQuantityBtn);

        // Create central panel to hold both brand and sort panels
        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        controlPanel.add(brandPanel);
        controlPanel.add(sortPanel);

        // Create table
        String[] columns = {"ID", "Product", "Quantity", "Total", "Date", "Brand"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        salesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);

        // Bottom panel with total
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("Total Sales: $0.00");
        bottomPanel.add(totalLabel);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        frame.add(mainPanel);
        frame.setVisible(true);
        
        // Initially load all sales
        updateTable(null);
    }

    private void updateTable(String brandName) {
        tableModel.setRowCount(0);
        String[][] sales;
        double totalSales = 0.0;
        
        try {
            if (brandName == null) {
                sales = DatabaseHandler.getSales();
                for (String[] sale : sales) {
                    tableModel.addRow(sale);
                    totalSales += Double.parseDouble(sale[3]);
                }
            } else {
                sales = DatabaseHandler.getSalesByBrand(brandName);
                totalSales = DatabaseHandler.getTotalSalesByBrand(brandName);
                for (String[] sale : sales) {
                    tableModel.addRow(sale);
                }
            }
    
            // Update total sales label directly using the class field
            totalLabel.setText(String.format("Total Sales: $%.2f", totalSales));
        } catch (Exception e) {
            System.out.println("Error updating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add new method for sorting
    private void sortTable(int column) {
        int rowCount = tableModel.getRowCount();
        Object[][] data = new Object[rowCount][6];
        
        // Copy data to array
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < 6; j++) {
                data[i][j] = tableModel.getValueAt(i, j);
            }
        }

        // Sort data
        java.util.Arrays.sort(data, (a, b) -> {
            if (column == 4) { // Date column
                return ((String)b[column]).compareTo((String)a[column]); // newest first
            } else { // Numeric columns (Total or Quantity)
                double val1 = Double.parseDouble(a[column].toString());
                double val2 = Double.parseDouble(b[column].toString());
                return Double.compare(val2, val1); // highest first
            }
        });

        // Update table with sorted data
        tableModel.setRowCount(0);
        double totalSales = 0.0;
        for (Object[] row : data) {
            tableModel.addRow(row);
            totalSales += Double.parseDouble(row[3].toString());
        }

        // Update total
        totalLabel.setText(String.format("Total Sales: $%.2f", totalSales));
    }
}