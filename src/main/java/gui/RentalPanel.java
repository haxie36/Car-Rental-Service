package gui;

import rental.Rental;
import rental.RentalService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class RentalPanel extends JPanel {
    private final RentalService rentalService;
    private JTextArea outputTextArea;
    private JTextField idField, carIdField, clientIdField;

    public RentalPanel(RentalService rentalService) {
        this.rentalService = rentalService;
        initialize();
    }

    //Створення самої вкладки
    private void initialize() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        //Панель керування
        JPanel controlPanel = createControlPanel();

        //Текстова область для виводу
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setPreferredSize(new Dimension(600, 0));

        add(controlPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
    }

    //Створення панелі з кнопками
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Керування орендами"));

        //Кнопки управління
        JButton addRentalBtn = createStyledButton("Додати оренду");
        JButton removeRentalBtn = createStyledButton("Видалити оренду");
        JButton removeClientRentalsBtn = createStyledButton("Видалити оренди клієнта");
        JButton removeCarRentalsBtn = createStyledButton("Видалити оренди автомобіля");
        JButton findRentalByIdBtn = createStyledButton("Пошук оренди за ID");
        JButton findClientRentalsBtn = createStyledButton("Оренди клієнта");
        JButton findCarRentalsBtn = createStyledButton("Оренди автомобіля");
        JButton listRentalsBtn = createStyledButton("Список всіх оренд");

        //Поля для вводу
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Поля для пошуку"));
        inputPanel.setMaximumSize(new Dimension(250, 100));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(new JLabel("ID оренди:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("ID автомобіля:"));
        carIdField = new JTextField();
        inputPanel.add(carIdField);

        inputPanel.add(new JLabel("ID клієнта:"));
        clientIdField = new JTextField();
        inputPanel.add(clientIdField);

        //Додавання елементів з відступами
        panel.add(addRentalBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(removeRentalBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(removeClientRentalsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(removeCarRentalsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(findRentalByIdBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(findClientRentalsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(findCarRentalsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(listRentalsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(inputPanel);

        //Обробники подій
        addRentalBtn.addActionListener(e -> showAddRentalDialog());
        removeRentalBtn.addActionListener(e -> removeRental());
        removeClientRentalsBtn.addActionListener(e -> removeAllClientRentals());
        removeCarRentalsBtn.addActionListener(e -> removeAllCarRentals());
        findRentalByIdBtn.addActionListener(e -> findRentalById());
        findClientRentalsBtn.addActionListener(e -> findClientRentals());
        findCarRentalsBtn.addActionListener(e -> findCarRentals());
        listRentalsBtn.addActionListener(e -> listAllRentals());

        panel.setPreferredSize(new Dimension(250, 600));
        return panel;
    }

    //Допоміжний метод, що форматує кнопки
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 30));
        button.setPreferredSize(new Dimension(200, 30));
        return button;
    }

    //Створення вікна додавання оренди
    private void showAddRentalDialog() {
        AddRentalDialog dialog = new AddRentalDialog(rentalService);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            outputTextArea.setText("Оренду успішно додано!\n\n" + getLastRentalInfo());
        }
    }

    //Отримання інформації про останню додану оренду
    private String getLastRentalInfo() {
        if (!rentalService.findAllRentals().isEmpty()) {
            List<Rental> rentals = rentalService.findAllRentals();
            Rental lastRental = rentals.getLast();
            return formatRentalInfo(lastRental);
        }
        return "Інформація недоступна";
    }

    //Видалення оренди
    private void removeRental() {
        String rentalId = idField.getText().trim();
        if (rentalId.isEmpty()) {
            showOutput("Помилка: Введіть ID оренди!");
            return;
        }

        try { //Основна частина
            rentalService.removeRental(rentalId);
            showOutput("Оренду успішно видалено!");
            clearFields();
        } catch (Exception ex) {
            showOutput("Помилка: " + ex.getMessage());
        }
    }

    //Пошук оренд клієнта за введеним у поле Id
    private void removeAllClientRentals() {
        String clientId = clientIdField.getText().trim();
        if (clientId.isEmpty()) {
            showOutput("Помилка: Введіть ID клієнта!");
            return;
        }

        try { //Основна частина
            rentalService.removeAllRentalsOfClient(clientId);
            showOutput("Усі оренди клієнта успішно видалено!");
        } catch (Exception ex) {
            showOutput("Помилка: " + ex.getMessage());
        }
    }

    //Пошук оренд на автівку за введеним у поле Id
    private void removeAllCarRentals() {
        String carId = carIdField.getText().trim();
        if (carId.isEmpty()) {
            showOutput("Помилка: Введіть ID автомобіля!");
            return;
        }

        try { //Основна частина
            rentalService.removeAllCarRentals(carId);
            showOutput("Усі оренди автомобіля успішно видалено!");
        } catch (Exception ex) {
            showOutput("Помилка: " + ex.getMessage());
        }
    }

    //Пошук оренди за Id
    private void findRentalById() {
        String rentalId = idField.getText().trim();
        if (rentalId.isEmpty()) {
            showOutput("Помилка: Введіть ID оренди!");
            return;
        }

        //Отримання оренди та вивід інформації на екран
        Rental rental = rentalService.findRental(rentalId);
        if (rental != null) {
            showOutput("Знайдено оренду:\n" + formatRentalInfo(rental));
        } else {
            showOutput("Оренду з ID " + rentalId + " не знайдено.");
        }
    }

    //Пошук оренд клієнта за введеним Id
    private void findClientRentals() {
        String clientId = clientIdField.getText().trim();
        if (clientId.isEmpty()) {
            showOutput("Помилка: Введіть ID клієнта!");
            return;
        }

        //Отримання списку оренд та виведення інформації на екран
        List<Rental> rentals = rentalService.findAllRentalsByClientId(clientId);
        displayRentals(rentals, "клієнта " + clientId);
    }

    //Пошук оренд на автівку за введеним Id
    private void findCarRentals() {
        String carId = carIdField.getText().trim();
        if (carId.isEmpty()) {
            showOutput("Помилка: Введіть ID автомобіля!");
            return;
        }

        //Отримання списку оренд та виведення інформації на екран
        List<Rental> rentals = rentalService.findAllRentalsByCarId(carId);
        displayRentals(rentals, "автомобіля " + carId);
    }

    //Вивід на екран інформації про всі оренди
    private void listAllRentals() {
        List<Rental> allRentals = rentalService.findAllRentals();
        displayRentals(allRentals, "всіх оренд");
    }

    //Форматований вивід деякого списку оренд на екран
    private void displayRentals(List<Rental> rentals, String type) {
        if (rentals != null && !rentals.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("=== ОРЕНДИ ").append(type.toUpperCase()).append(" ===\n\n");
            for (Rental rental : rentals) {
                sb.append(formatRentalInfo(rental));
                sb.append("\n").append("=".repeat(50)).append("\n");
            }
            sb.append("\nВсього знайдено: ").append(rentals.size()).append(" оренд");
            showOutput(sb.toString());
        } else {
            showOutput("Оренд для " + type + " не знайдено.");
        }
    }

    //Форматування інформації про оренду
    private String formatRentalInfo(Rental rental) {
        return "ID оренди: " + rental.getId() + "\n" +
                "Автомобіль: " + rental.getCarId() + "\n" +
                "Клієнт: " + rental.getClientId() + "\n" +
                "Період: " + rental.getStartDate() + " - " + rental.getEndDate() + "\n" +
                "Тривалість: " + rental.totalDays() + " дн.\n" +
                "Загальна вартість: " + String.format("%.2f", rental.getTotalPrice()) + " грн\n" +
                "Статус: " + (rental.isActive() ? "Активна" : "Завершена/запланована");
    }

    //Вивід тексту на екран (поле виводу)
    private void showOutput(String text) {
        outputTextArea.setText(text);
    }

    //Очищення полів вводу
    private void clearFields() {
        idField.setText("");
        carIdField.setText("");
        clientIdField.setText("");
    }
}