package gui;

import rental.RentalService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddClientDialog extends JDialog {
    private final RentalService rentalService;
    private boolean success = false;

    private JTextField nameField;
    private JTextField phoneField;
    private JLabel statusLabel;

    public AddClientDialog(RentalService rentalService) {
        this.rentalService = rentalService;
        initialize();
    }

    private void initialize() {
        setTitle("Додати нового клієнта");
        setModal(true);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //Головна панель
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //Поля вводу
        mainPanel.add(new JLabel("ПІБ:"));
        nameField = new JTextField();
        mainPanel.add(nameField);

        mainPanel.add(new JLabel("Телефон:"));
        phoneField = new JTextField();
        mainPanel.add(phoneField);

        mainPanel.add(new JLabel("Статус:"));
        statusLabel = new JLabel("Введіть дані");
        statusLabel.setForeground(Color.BLUE);
        mainPanel.add(statusLabel);

        //Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        JButton addButton = new JButton("Додати");
        JButton cancelButton = new JButton("Скасувати");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addClient();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addClient() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            statusLabel.setText("Заповніть всі поля!");
            statusLabel.setForeground(Color.RED);
            return;
        }

        //Перевірка формату телефону (базова)
        if (!phone.matches("\\+?[0-9\\-()\\s]+")) {
            statusLabel.setText("Невірний формат телефону!");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            rentalService.addClientDirect(name, phone);
            success = true;
            dispose();
        } catch (Exception ex) {
            statusLabel.setText("Помилка: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}