package gui;

import rental.RentalService;

import javax.swing.*;
import java.awt.*;

public class RentalGUI {
    private final RentalService rentalService;
    private JFrame mainFrame;

    public RentalGUI() {
        rentalService = new RentalService();
        initialize();
        boolean unauthorized = true;
        while (unauthorized) {
            unauthorized = showLoginDialog();
        }
    }

    //Створення вікна програми з усіма вкладками
    private void initialize() {
        mainFrame = new JFrame("Система для обліку оренди авто");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700); //Зменшений розмір
        mainFrame.setLocationRelativeTo(null);

        //Створення вкладок
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 12));

        //Створення панелей через окремі класи
        ClientPanel clientPanel = new ClientPanel(rentalService);
        CarPanel carPanel = new CarPanel(rentalService);
        RentalPanel rentalPanel = new RentalPanel(rentalService);
        ReportsPanel reportsPanel = new ReportsPanel(rentalService);

        //Додавання вкладок
        tabbedPane.addTab("Клієнти", clientPanel);
        tabbedPane.addTab("Автомобілі", carPanel);
        tabbedPane.addTab("Оренди", rentalPanel);
        tabbedPane.addTab("Звіти", reportsPanel);

        //Кнопка перезавантаження БД
        JButton reloadButton = new JButton("Перезавантажити БД");
        reloadButton.addActionListener(e -> {
            rentalService.reloadData();
            JOptionPane.showMessageDialog(mainFrame, "Базу даних перезавантажено!");
        });

        //Панель з кнопкою
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        bottomPanel.add(reloadButton);

        //Основне компонування
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(tabbedPane, BorderLayout.CENTER);
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);
    }

    //Створення вікна логіну
    private boolean showLoginDialog() {
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
                "Введіть пароль:",
                passwordField
        };

        int option = JOptionPane.showConfirmDialog(
                null, message, "Вхід до системи",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            String password = new String(passwordField.getPassword());
            if ("supersecretpasswordalsoknownasssp".equals(password)) {
                mainFrame.setVisible(true);
                return false;
            } else {
                JOptionPane.showMessageDialog(null, "Невірний пароль!", "Помилка", JOptionPane.ERROR_MESSAGE);
                return true;
            }
        } else {
            System.exit(0);
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RentalGUI::new);
    }
}