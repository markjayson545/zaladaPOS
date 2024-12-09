package MainUI;

import java.awt.*;
import javax.swing.*;

import Database.DatabaseHandler;
import UserMode.AdminMode;

import java.awt.event.*;

public class Main {
    public void showUserMode() {
        JFrame frame;
        frame = new JFrame("POS System");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;

        JPanel panel = new JPanel();
        JButton userButton = new JButton("User Mode");
        JButton adminButton = new JButton("Admin Mode");

        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UserMode.UserMode userMode = new UserMode.UserMode();
                userMode.showUserMode();
                frame.dispose(); // Move this after creating and showing UserMode
            }
        });

        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AdminMode.showAdminMode();
            }
        });

        panel.add(userButton);
        panel.add(adminButton);
        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Main main = new Main();
        DatabaseHandler.createDefaultItems(); // Create items first
        DatabaseHandler.createDefaultSales(); // Then create sales table
        main.showUserMode();
    }
}
