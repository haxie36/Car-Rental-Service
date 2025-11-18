package gui;

import rental.Car;
import rental.RentalService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CarPanel extends JPanel {
    private final RentalService rentalService;
    private JTextArea outputTextArea;
    private JTextField idField;

    public CarPanel(RentalService rentalService) {
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
        panel.setBorder(BorderFactory.createTitledBorder("Керування автомобілями"));
        panel.setPreferredSize(new Dimension(250, 600));

        JButton addCarBtn = createStyledButton("Додати автомобіль");
        JButton removeCarBtn = createStyledButton("Видалити автомобіль");
        JButton findCarByIdBtn = createStyledButton("Пошук автомобіля за ID");
        JButton listCarsBtn = createStyledButton("Список всіх автомобілів");
        JButton listAvailableCarsBtn = createStyledButton("Доступні автомобілі на дати");

        //Поле для вводу ID
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Пошук за ID"));
        inputPanel.setMaximumSize(new Dimension(250, 60));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        idPanel.add(new JLabel("ID:"));
        idField = new JTextField(10);
        idPanel.add(idField);
        inputPanel.add(idPanel, BorderLayout.NORTH);

        //Додавання елементів
        panel.add(addCarBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(removeCarBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(findCarByIdBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(listCarsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(listAvailableCarsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(inputPanel);

        //Обробники подій
        addCarBtn.addActionListener(e -> showAddCarDialog());
        removeCarBtn.addActionListener(e -> removeCar());
        findCarByIdBtn.addActionListener(e -> findCarById());
        listCarsBtn.addActionListener(e -> listAllCars());
        listAvailableCarsBtn.addActionListener(e -> showAvailableCarsDialog());

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

    //Створення вікна додавання автівки
    private void showAddCarDialog() {
        AddCarDialog dialog = new AddCarDialog(rentalService);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            showOutput("Автомобіль успішно додано!\n\n" + getLastCarInfo());
        }
    }

    //Отримання інформації про останню додану автівку
    private String getLastCarInfo() {
        List<Car> cars = rentalService.findAllCars();
        if (!cars.isEmpty()) {
            Car lastCar = cars.getLast();
            return formatCarInfo(lastCar);
        }
        return "Інформація недоступна";
    }

    //Видалення автівки
    private void removeCar() {
        String carId = idField.getText().trim();
        if (carId.isEmpty()) {
            showOutput("Помилка: Введіть ID автомобіля!");
            return;
        }

        try { //Основна частина
            rentalService.removeCar(carId);
            showOutput("Автомобіль успішно видалено!");
            clearFields();
        } catch (Exception ex) {
            showOutput("Помилка: " + ex.getMessage());
        }
    }

    //Пошук автівки за Id
    private void findCarById() {
        String carId = idField.getText().trim();
        if (carId.isEmpty()) {
            showOutput("Помилка: Введіть ID автомобіля!");
            return;
        }

        //Отримання автівки та вивід інформації на екран
        Car car = rentalService.findCar(carId);
        if (car != null) {
            showOutput("Знайдено автомобіль:\n" + formatCarInfo(car));
        } else {
            showOutput("Автомобіль з ID " + carId + " не знайдено.");
        }
    }

    //Вивід інформації про всі автівки на екран
    private void listAllCars() {
        List<Car> cars = rentalService.findAllCars();
        if (cars != null && !cars.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("=== ВСІ АВТОМОБІЛІ ===\n\n");
            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                sb.append("[").append(i + 1).append("] ").append(formatCarInfo(car));
                sb.append("\n").append("-".repeat(50)).append("\n");
            }
            sb.append("\nВсього автомобілів: ").append(cars.size());
            showOutput(sb.toString());
        } else {
            showOutput("Автомобілів не знайдено.");
        }
    }

    //Пошук доступних автівок на проміжку часу
    private void showAvailableCarsDialog() {
        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField();

        Object[] message = {
                "Дата початку (YYYY-MM-DD):", startDateField,
                "Дата кінця (YYYY-MM-DD):", endDateField
        };

        //Введення дат
        int option = JOptionPane.showConfirmDialog(
                this, message, "Пошук доступних автомобілів",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try { //Отримання дат та пошук автівок
                LocalDate startDate = LocalDate.parse(startDateField.getText());
                LocalDate endDate = LocalDate.parse(endDateField.getText());

                List<Car> availableCars = rentalService.findAvailableCars(startDate, endDate);
                displayAvailableCars(availableCars, startDate, endDate);

            } catch (Exception ex) {
                showOutput("Помилка: Невірний формат дати!");
            }
        }
    }

    //Вивід на екран доступних автівок на деякі дати
    private void displayAvailableCars(List<Car> cars, LocalDate startDate, LocalDate endDate) {
        if (cars != null && !cars.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);

            sb.append("=== ДОСТУПНІ АВТОМОБІЛІ ===\n");
            sb.append("Період: ").append(startDate).append(" - ").append(endDate);
            sb.append(" (").append(days).append(" дн.)\n\n");

            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                sb.append("[").append(i + 1).append("] ").append(formatCarInfo(car));
                double totalPrice = car.getPricePerDay() * days;
                sb.append("Вартість оренди: ").append(String.format("%.2f", totalPrice));
                sb.append(" грн (").append(car.getPricePerDay()).append(" грн/день)\n");
                sb.append("-".repeat(50)).append("\n");
            }
            sb.append("\nЗнайдено ").append(cars.size()).append(" доступних автомобілів");
            showOutput(sb.toString());
        } else {
            showOutput("На жаль, немає доступних автомобілів на обраний період.");
        }
    }

    //Форматування інформації про автівку
    private String formatCarInfo(Car car) {
        return "ID: " + car.getId() + "\n" +
                "Марка: " + car.getBrand() + "\n" +
                "Модель: " + car.getModel() + "\n" +
                "Рік: " + car.getYear() + "\n" +
                "Пробіг: " + car.getMileage() + " км\n" +
                "Ціна за день: " + car.getPricePerDay() + " грн\n";
    }

    //Вивід тексту на екран (поле виводу)
    private void showOutput(String text) {
        outputTextArea.setText(text);
    }

    //Очищення полів вводу
    private void clearFields() {
        idField.setText("");
    }
}