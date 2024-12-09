package UserMode;

import javax.swing.*;
import java.awt.*;

public class AdminMode {
    private static JFrame frame;

    public static void showAdminMode() {
        frame = new JFrame("Admin Mode");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Add header panel with back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> frame.dispose());
        headerPanel.add(backBtn, BorderLayout.WEST);

        // Main content panel
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton viewSalesButton = new JButton("View Sales");
        JButton manageItemsButton = new JButton("Manage Items");

        viewSalesButton.addActionListener(e -> {
            SalesView salesView = new SalesView();
            salesView.showSalesView();
        });

        manageItemsButton.addActionListener(e -> {
            ManageItems manageItems = new ManageItems();
            manageItems.showManageItems();
        });

        panel.add(viewSalesButton);
        panel.add(manageItemsButton);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
