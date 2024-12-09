package UserMode;

import Database.DatabaseHandler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class SalesView {
    private JFrame frame;
    private JTable salesTable;
    private DefaultTableModel tableModel;

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
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(brandPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add total sales label at the bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total Sales: $0.00");
        bottomPanel.add(totalLabel);
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
                    totalSales += Double.parseDouble(sale[3]); // Changed from sale[4] to sale[3] to match array structure
                }
            } else {
                sales = DatabaseHandler.getSalesByBrand(brandName);
                totalSales = DatabaseHandler.getTotalSalesByBrand(brandName);
                for (String[] sale : sales) {
                    tableModel.addRow(sale);
                }
            }
    
            // Update total sales label
            JPanel mainPanel = (JPanel) frame.getContentPane().getComponent(0);
            JPanel bottomPanel = (JPanel) mainPanel.getComponent(2); // Get the bottom panel
            JLabel totalLabel = (JLabel) bottomPanel.getComponent(0);
            totalLabel.setText(String.format("Total Sales: $%.2f", totalSales));
        } catch (Exception e) {
            System.out.println("Error updating table: " + e.getMessage());
            e.printStackTrace();
        }
    }
}