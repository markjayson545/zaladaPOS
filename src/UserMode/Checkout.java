package UserMode;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import Database.DatabaseHandler;

public class Checkout {
    private JDialog dialog;
    private DefaultTableModel tableModel;
    private JTable table;
    private double total = 0.0;
    private JLabel totalLabel;

    public Checkout(JFrame parent, Object[][] selectedItems) {
        dialog = new JDialog(parent, "Checkout", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setAlwaysOnTop(true);

        // Create table model
        String[] columns = {"ID", "Name", "Price", "Quantity", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        
        // Add selected items and calculate total
        for (Object[] item : selectedItems) {
            int itemId = Integer.parseInt((String)item[0]);
            String name = (String)item[1];
            double price = Double.parseDouble((String)item[2]);
            int quantity = (Integer)item[3];
            double subtotal = price * quantity;
            total += subtotal;
            
            tableModel.addRow(new Object[]{
                itemId, name, String.format("%.2f", price),
                quantity, String.format("%.2f", subtotal)
            });
        }

        // Add components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Order Summary", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        totalLabel = new JLabel("Total: $" + String.format("%.2f", total), SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm Payment");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> handlePayment());
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);

        bottomPanel.add(totalLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handlePayment() {
        int confirm = JOptionPane.showConfirmDialog(dialog,
                "Total Amount: $" + String.format("%.2f", total) + "\nConfirm payment?",
                "Payment Confirmation",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Process the sale
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = sdf.format(new Date());
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int itemId = (Integer)tableModel.getValueAt(i, 0);
                String name = (String)tableModel.getValueAt(i, 1);
                int quantity = (Integer)tableModel.getValueAt(i, 3);
                double subtotal = Double.parseDouble(((String)tableModel.getValueAt(i, 4)).replace(",", ""));
                
                // Get brand information from the database
                int brandId = DatabaseHandler.getBrandIdByItemId(itemId);
                String brandName = DatabaseHandler.getBrandNameById(brandId);
                
                DatabaseHandler.insertSale(itemId, brandId, brandName, name, quantity, subtotal, currentDate);
            }
            
            JOptionPane.showMessageDialog(dialog, "Payment successful!");
            dialog.dispose();
        }
    }

    public void showCheckout() {
        dialog.setVisible(true);
    }

}
