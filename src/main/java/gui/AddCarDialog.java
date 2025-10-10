package gui;

import rental.RentalService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddCarDialog extends JDialog {
    private final RentalService rentalService;
    private boolean success = false;

    private JTextField brandField, modelField, yearField, mileageField, priceField;
    private JLabel statusLabel;

    public AddCarDialog(RentalService rentalService) {
        this.rentalService = rentalService;
        initialize();
    }

    private void initialize() {
        setTitle("Додати новий автомобіль");
        setModal(true);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //Головна панель
        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //Поля вводу
        mainPanel.add(new JLabel("Марка:"));
        brandField = new JTextField();
        mainPanel.add(brandField);

        mainPanel.add(new JLabel("Модель:"));
        modelField = new JTextField();
        mainPanel.add(modelField);

        mainPanel.add(new JLabel("Рік:"));
        yearField = new JTextField();
        mainPanel.add(yearField);

        mainPanel.add(new JLabel("Пробіг:"));
        mileageField = new JTextField();
        mainPanel.add(mileageField);

        mainPanel.add(new JLabel("Ціна за день:"));
        priceField = new JTextField();
        mainPanel.add(priceField);

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
                addCar();
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

    private void addCar() {
        try {
            String brand = brandField.getText().trim();
            String model = modelField.getText().trim();
            String yearStr = yearField.getText().trim();
            String mileageStr = mileageField.getText().trim();
            String priceStr = priceField.getText().trim();

            if (brand.isEmpty() || model.isEmpty() || yearStr.isEmpty() || mileageStr.isEmpty() || priceStr.isEmpty()) {
                statusLabel.setText("Заповніть всі поля!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            int year, mileage;
            double price;

            try {
                year = Integer.parseInt(yearStr);
                mileage = Integer.parseInt(mileageStr);
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                statusLabel.setText("Невірний формат числа!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            //Перевірка року
            int currentYear = java.time.Year.now().getValue();
            if (year < 1900 || year > currentYear + 1) {
                statusLabel.setText("Невірний рік виробництва!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            //Перевірка пробігу та ціни
            if (mileage < 0 || price <= 0) {
                statusLabel.setText("Пробіг та ціна мають бути додатними!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            rentalService.addCarDirect(brand, model, year, mileage, price);
            success = true;
            dispose();

        } catch (NumberFormatException ex) {
            statusLabel.setText("Невірний формат числа!");
            statusLabel.setForeground(Color.RED);
        } catch (Exception ex) {
            statusLabel.setText("Помилка: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}