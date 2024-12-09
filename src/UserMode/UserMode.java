package UserMode;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import Database.DatabaseHandler;
import javax.swing.table.TableCellRenderer;

public class UserMode {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel totalLabel; // Add this field
    private int currentBrandId; // Add this field

    // Add new class for quantity panel
    class QuantityPanel extends JPanel {
        private JLabel quantityLabel;
        private int quantity = 1; // Changed initial value to 1
        private int maxStock;
        private JButton decreaseBtn;
        private JButton increaseBtn;

        public QuantityPanel(int stock) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
            this.maxStock = stock;
            
            decreaseBtn = new JButton("-");
            quantityLabel = new JLabel("1"); // Set initial label to 1
            increaseBtn = new JButton("+");

            decreaseBtn.addActionListener(e -> updateQuantity(-1));
            increaseBtn.addActionListener(e -> updateQuantity(1));

            // Initial button states
            decreaseBtn.setEnabled(false); // Initially disabled as quantity is 1
            increaseBtn.setEnabled(stock > 1); // Disabled if stock is 1 or less

            add(decreaseBtn);
            add(quantityLabel);
            add(increaseBtn);
        }

        private void updateQuantity(int delta) {
            int newQuantity = quantity + delta;
            if (newQuantity >= 1 && newQuantity <= maxStock) {
                quantity = newQuantity;
                quantityLabel.setText(String.valueOf(quantity));
                
                // Update button states
                decreaseBtn.setEnabled(quantity > 1);
                increaseBtn.setEnabled(quantity < maxStock);
                
                updateTotal();
            }
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int value) {
            quantity = value;
            quantityLabel.setText(String.valueOf(quantity));
        }
    }

    public void showUserMode() {
        // Initialize database first
        DatabaseHandler.createDefaultItems();

        frame = new JFrame("POS System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        showBrandSelection();
        frame.setVisible(true); // Move this after showBrandSelection
    }

    private void showBrandSelection() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Add title
        JLabel titleLabel = new JLabel("ZALADA POS SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        frame.add(titleLabel, BorderLayout.NORTH);

        // Create brands table
        DefaultTableModel brandTableModel = new DefaultTableModel(
            new String[]{"Brand Name", "Actions"}, 0
        );

        JTable brandTable = new JTable(brandTableModel);
        brandTable.setRowHeight(40);
        
        // Custom renderer for action buttons
        brandTable.getColumnModel().getColumn(1).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JButton button = new JButton("Select");
                button.setOpaque(true);
                return button;
            }
        });

        // Custom editor for action buttons
        brandTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()) {
            private JButton button = new JButton("Select");

            {
                button.addActionListener(e -> {
                    String brandName = (String) brandTableModel.getValueAt(
                        brandTable.getSelectedRow(), 0);
                    int brandId = DatabaseHandler.getBrandId(brandName);
                    showProductTable(brandId);
                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                return button;
            }
        });

        // Load brands into table
        for (String brand : DatabaseHandler.getBrands()) {
            brandTableModel.addRow(new Object[]{brand, ""});
        }

        // Layout
        frame.add(new JScrollPane(brandTable), BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void showProductTable(int brandId) {
        this.currentBrandId = brandId; // Store current brand ID
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Create header panel with back button
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton("Back to Brands");
        backBtn.addActionListener(e -> showBrandSelection());
        
        // Add title label
        JLabel titleLabel = new JLabel("ZALADA POS SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        topPanel.add(backBtn, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        frame.add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                if (column == 5) return QuantityPanel.class;
                return String.class;
            }
        };

        tableModel.addColumn("Select");
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Price");
        tableModel.addColumn("Stock");
        tableModel.addColumn("Quantity");

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(30);

        // Set custom renderer and editor for quantity column
        table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                return (Component) value;
            }
        });

        table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JTextField()) {
            private QuantityPanel panel;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                panel = (QuantityPanel) value;
                return panel;
            }

            @Override
            public Object getCellEditorValue() {
                return panel;
            }
        });

        loadTableData(brandId);

        JScrollPane scrollPane = new JScrollPane(table);

        // Add buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton checkoutButton = new JButton("Checkout");
        JButton resetButton = new JButton("Reset");

        checkoutButton.addActionListener(e -> handleCheckout());
        resetButton.addActionListener(e -> resetCheckboxes());

        buttonPanel.add(checkoutButton);
        buttonPanel.add(resetButton);

        // Add total panel between table and buttons
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        totalLabel = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalPanel.add(totalLabel);

        // Create a panel to hold both total and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add components to frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Add table listener for checkbox changes
        table.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 0) { // Checkbox column
                updateTotal();
            }
        });

        frame.revalidate();
        frame.repaint();
    }

    private void loadTableData(int brandId) {
        tableModel.setRowCount(0); // Clear existing rows
        String[][] items = DatabaseHandler.getItems(brandId);
        System.out.println("Loaded items array length: " + items.length);

        if (items.length > 0) {
            for (String[] item : items) {
                tableModel.addRow(new Object[] {
                    false,
                    item[0],
                    item[1],
                    item[2],
                    item[3],
                    new QuantityPanel(Integer.parseInt(item[3])) // Pass stock value to QuantityPanel
                });
                System.out.println("Added to table: " + item[1]);
            }
        } else {
            System.out.println("No items found in database");
        }
    }

    private void handleCheckout() {
        int rowCount = tableModel.getRowCount();
        java.util.List<Object[]> selectedItems = new java.util.ArrayList<>();
        
        for (int i = 0; i < rowCount; i++) {
            Boolean checked = (Boolean) tableModel.getValueAt(i, 0);
            if (checked) {
                QuantityPanel qPanel = (QuantityPanel) tableModel.getValueAt(i, 5);
                int quantity = qPanel.getQuantity();
                if (quantity > 0) {
                    selectedItems.add(new Object[]{
                        tableModel.getValueAt(i, 1),  // ID
                        tableModel.getValueAt(i, 2),  // Name
                        tableModel.getValueAt(i, 3),  // Price
                        quantity
                    });
                }
            }
        }
        
        if (selectedItems.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select items to checkout");
            return;
        }

        Checkout checkout = new Checkout(
            (JFrame) SwingUtilities.getWindowAncestor(table),
            selectedItems.toArray(new Object[0][])
        );
        checkout.showCheckout();
        
        // Reset after checkout
        resetCheckboxes();
        loadTableData(currentBrandId); // Pass the current brand ID
    }

    private void resetCheckboxes() {
        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            tableModel.setValueAt(false, i, 0);
        }
    }

    // Add this new method to calculate and update the total
    private void updateTotal() {
        double total = 0.0;
        int rowCount = tableModel.getRowCount();
        
        for (int i = 0; i < rowCount; i++) {
            Boolean checked = (Boolean) tableModel.getValueAt(i, 0);
            if (checked) {
                QuantityPanel qPanel = (QuantityPanel) tableModel.getValueAt(i, 5);
                int quantity = qPanel.getQuantity();
                double price = Double.parseDouble((String) tableModel.getValueAt(i, 3));
                total += price * quantity;
            }
        }
        
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

}
