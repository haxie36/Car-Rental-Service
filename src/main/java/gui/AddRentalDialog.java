package gui;

import rental.Car;
import rental.Client;
import rental.RentalService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddRentalDialog extends JDialog {
    private final RentalService rentalService;
    private boolean success = false;

    private JTextField carIdField, clientIdField, startDateField, endDateField;
    private JLabel statusLabel;

    public AddRentalDialog(RentalService rentalService) {
        this.rentalService = rentalService;
        initialize();
    }

    private void initialize() {
        setTitle("Додати нову оренду");
        setModal(true);
        setSize(450, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        //Головна панель з відступами
        JPanel mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //Поля вводу
        mainPanel.add(new JLabel("ID автомобіля:"));
        carIdField = new JTextField();
        mainPanel.add(carIdField);

        mainPanel.add(new JLabel("ID клієнта:"));
        clientIdField = new JTextField();
        mainPanel.add(clientIdField);

        mainPanel.add(new JLabel("Дата початку (YYYY-MM-DD):"));
        startDateField = new JTextField();
        mainPanel.add(startDateField);

        mainPanel.add(new JLabel("Дата кінця (YYYY-MM-DD):"));
        endDateField = new JTextField();
        mainPanel.add(endDateField);

        //Статус
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
                addRental();
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

    private void addRental() {
        try {
            String carId = carIdField.getText().trim();
            String clientId = clientIdField.getText().trim();
            String startDateStr = startDateField.getText().trim();
            String endDateStr = endDateField.getText().trim();

            //Перевірка заповненості полів
            if (carId.isEmpty() || clientId.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty()) {
                statusLabel.setText("Заповніть всі поля!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            //Перевірка дат
            LocalDate startDate, endDate;
            try {
                startDate = LocalDate.parse(startDateStr);
                endDate = LocalDate.parse(endDateStr);
            } catch (DateTimeParseException e) {
                statusLabel.setText("Невірний формат дати!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            if (endDate.isBefore(startDate)) {
                statusLabel.setText("Дата кінця не може бути раніше початку!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            //Перевірка наявності авто та клієнта
            Car car = rentalService.findCar(carId);
            Client client = rentalService.findClient(clientId);

            if (car == null) {
                statusLabel.setText("Автомобіль не знайдено!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            if (client == null) {
                statusLabel.setText("Клієнт не знайдено!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            //Перевірка доступності авто
            if (!car.isAvailable(startDate, endDate)) {
                statusLabel.setText("Автомобіль недоступний на ці дати!");
                statusLabel.setForeground(Color.RED);
                return;
            }

            //Створення оренди
            boolean result = rentalService.addRentalDirect(carId, clientId, startDate, endDate);

            if (result) {
                success = true;
                dispose();
            } else {
                statusLabel.setText("Помилка при додаванні оренди!");
                statusLabel.setForeground(Color.RED);
            }

        } catch (Exception ex) {
            statusLabel.setText("Помилка: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}