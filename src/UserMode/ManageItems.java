package UserMode;

import Database.DatabaseHandler;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class ManageItems {
    private JFrame frame;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private int currentBrandId;

    public void showManageItems() {
        frame = new JFrame("Manage Items");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        showBrandSelection();
        frame.setVisible(true);
    }

    private void showBrandSelection() {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Manage Brands and Items", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton addBrandBtn = new JButton("Add New Brand");
        addBrandBtn.addActionListener(e -> showAddBrandDialog());
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(addBrandBtn, BorderLayout.EAST);

        // Create brands table
        String[] columns = {"Brand ID", "Brand Name", "Actions"};
        DefaultTableModel brandTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        JTable brandTable = new JTable(brandTableModel);
        brandTable.setRowHeight(35);
        
        // Set up the action column
        brandTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        brandTable.getColumnModel().getColumn(2).setCellEditor(new BrandButtonEditor(new JTextField()));

        // Load brands into table
        loadBrandsIntoTable(brandTableModel);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(brandTable), BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    private void loadBrandsIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            String[][] brands = DatabaseHandler.getAllBrands();
            for (String[] brand : brands) {
                model.addRow(new Object[]{brand[0], brand[1], "Manage Items"});
            }
        } catch (Exception e) {
            System.out.println("Error loading brands: " + e.getMessage());
        }
    }

    private void showAddBrandDialog() {
        JDialog dialog = new JDialog(frame, "Add New Brand", true);
        dialog.setLayout(new GridLayout(0, 1, 5, 5));
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(frame);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField brandField = new JTextField();
        inputPanel.add(new JLabel("Brand Name:"), BorderLayout.WEST);
        inputPanel.add(brandField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            String brandName = brandField.getText().trim();
            if (!brandName.isEmpty()) {
                DatabaseHandler.insertBrand(brandName);
                dialog.dispose();
                showBrandSelection(); // Refresh the brand list
            } else {
                JOptionPane.showMessageDialog(dialog, "Please enter a brand name");
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(inputPanel);
        dialog.add(buttonPanel);
        dialog.setVisible(true);
    }

    private void showItemsTable(String brandName) {
        frame.getContentPane().removeAll();
        frame.setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Managing " + brandName + " Items", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton backBtn = new JButton("Back to Brands");
        backBtn.addActionListener(e -> showBrandSelection());
        
        headerPanel.add(backBtn, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Table
        String[] columns = {"ID", "Name", "Price", "Stock", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        
        itemsTable = new JTable(tableModel);
        itemsTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        itemsTable.getColumnModel().getColumn(4).setCellEditor(
            new ButtonEditor(new JTextField(), row -> showEditDialog(row)));
        
        loadTableData();

        // Add Item Button
        JButton addItemBtn = new JButton("Add New Item");
        addItemBtn.addActionListener(e -> showAddItemDialog());

        // Layout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(addItemBtn);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(itemsTable), BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        frame.revalidate();
        frame.repaint();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        String[][] items = DatabaseHandler.getItems(currentBrandId);
        for (String[] item : items) {
            tableModel.addRow(new Object[]{item[0], item[1], item[2], item[3], "Edit"});
        }
    }

    private void showAddItemDialog() {
        JDialog dialog = new JDialog(frame, "Add New Item", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(frame);

        dialog.add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        dialog.add(nameField);

        dialog.add(new JLabel("Price:"));
        JTextField priceField = new JTextField();
        dialog.add(priceField);

        dialog.add(new JLabel("Stock:"));
        JTextField stockField = new JTextField();
        dialog.add(stockField);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                
                // Add to database with current brand ID
                DatabaseHandler.insertItem(currentBrandId, name, price, stock);
                loadTableData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input format");
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(saveBtn);
        dialog.add(cancelBtn);
        dialog.setVisible(true);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private java.util.function.Consumer<Integer> action; // Change to Integer consumer
        private JTable table;

        public ButtonEditor(JTextField textField, java.util.function.Consumer<Integer> action) {
            super(textField);
            this.action = action;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table;
            label = value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && table != null) {
                action.accept(table.getSelectedRow());
            }
            isPushed = false;
            return label;
        }
    }

    class BrandButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table; // Add table reference

        public BrandButtonEditor(JTextField textField) {
            super(textField);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table; // Store table reference
            label = value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && table != null) {
                String brandName = (String) table.getModel().getValueAt(
                    table.getSelectedRow(), 1);
                currentBrandId = DatabaseHandler.getBrandId(brandName);
                showItemsTable(brandName);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void showEditDialog(int row) {
        String id = tableModel.getValueAt(row, 0).toString();
        String name = tableModel.getValueAt(row, 1).toString();
        String price = tableModel.getValueAt(row, 2).toString();
        String stock = tableModel.getValueAt(row, 3).toString();

        JDialog dialog = new JDialog(frame, "Edit Item", true);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(frame);

        dialog.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(name);
        dialog.add(nameField);

        dialog.add(new JLabel("Price:"));
        JTextField priceField = new JTextField(price);
        dialog.add(priceField);

        dialog.add(new JLabel("Stock:"));
        JTextField stockField = new JTextField(stock);
        dialog.add(stockField);

        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton cancelBtn = new JButton("Cancel");

        updateBtn.addActionListener(e -> {
            try {
                DatabaseHandler.updateItem(Integer.parseInt(id), 
                    nameField.getText(),
                    Double.parseDouble(priceField.getText()),
                    Integer.parseInt(stockField.getText()));
                loadTableData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input format");
            }
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                "Are you sure you want to delete this item?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseHandler.deleteItem(Integer.parseInt(id));
                loadTableData();
                dialog.dispose();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.add(updateBtn);
        dialog.add(deleteBtn);
        dialog.add(cancelBtn);
        dialog.setVisible(true);
    }
}