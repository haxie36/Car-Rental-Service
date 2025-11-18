package gui;

import rental.Car;
import rental.Client;
import rental.Rental;
import rental.RentalService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ReportsPanel extends JPanel {
    private final RentalService rentalService;
    private JTextArea outputTextArea;

    public ReportsPanel(RentalService rentalService) {
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
        panel.setBorder(BorderFactory.createTitledBorder("Звіти та статистика"));

        //Кнопки звітів
        JButton rentalsOnDateBtn = createStyledButton("Кількість оренд на дату");
        JButton avgRentalPriceBtn = createStyledButton("Середня ціна оренди");
        JButton highestMileageCarBtn = createStyledButton("Авто з найбільшим пробігом");
        JButton clientMostRentalsBtn = createStyledButton("Клієнт з найбільшою кількістю оренд");
        JButton longestRentalBtn = createStyledButton("Найтриваліша оренда");
        JButton showAllDataBtn = createStyledButton("Показати всі дані");
        JButton financialReportBtn = createStyledButton("Фінансовий звіт");
        JButton activeRentalsBtn = createStyledButton("Активні оренди");

        //Додавання елементів з відступами
        panel.add(rentalsOnDateBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(avgRentalPriceBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(highestMileageCarBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(clientMostRentalsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(longestRentalBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(showAllDataBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(financialReportBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(activeRentalsBtn);

        //Обробники подій
        rentalsOnDateBtn.addActionListener(e -> showRentalsOnDateDialog());
        avgRentalPriceBtn.addActionListener(e -> showAverageRentalPrice());
        highestMileageCarBtn.addActionListener(e -> showCarWithHighestMileage());
        clientMostRentalsBtn.addActionListener(e -> showClientWithMostRentals());
        longestRentalBtn.addActionListener(e -> showLongestRental());
        showAllDataBtn.addActionListener(e -> showAllData());
        financialReportBtn.addActionListener(e -> showFinancialReport());
        activeRentalsBtn.addActionListener(e -> showActiveRentals());

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

    //Створення вікна для вводу дати, за якою буде знайдено активні оренди на цю дату
    private void showRentalsOnDateDialog() {
        JTextField dateField = new JTextField(LocalDate.now().toString());

        Object[] message = {
                "Введіть дату (YYYY-MM-DD):", dateField
        };

        //Вікно вводу
        int option = JOptionPane.showConfirmDialog(
                this, message, "Кількість оренд на дату",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            try { //Пошук та вивід інформації на екран
                LocalDate date = LocalDate.parse(dateField.getText());
                int count = rentalService.rentalsOn(date);

                StringBuilder sb = new StringBuilder();
                sb.append("=== ЗВІТ: ОРЕНДИ НА ДАТУ ===\n\n");
                sb.append("Дата: ").append(date).append("\n");
                sb.append("Кількість активних оренд: ").append(count).append("\n\n");

                //Додавання інформації про оренди на дату
                List<Rental> allRentals = rentalService.findAllRentals();
                int activeCount = 0;
                for (Rental rental : allRentals) {
                    if ((date.equals(rental.getStartDate()) || date.isAfter(rental.getStartDate())) &&
                            (date.equals(rental.getEndDate()) || date.isBefore(rental.getEndDate()))) {
                        activeCount++;
                        sb.append("[").append(activeCount).append("] ").append(formatRentalInfo(rental));
                        sb.append("\n").append("-".repeat(50)).append("\n");
                    }
                }

                showOutput(sb.toString());
            } catch (Exception ex) {
                showOutput("Помилка: Невірний формат дати!");
            }
        }
    }

    //Середня ціна оренд
    private void showAverageRentalPrice() {
        double average = rentalService.averageRentalPrice();

        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗВІТ: СЕРЕДНЯ ЦІНА ОРЕНДИ ===\n\n");
        sb.append("Середня ціна оренди: ").append(String.format("%.2f", average)).append(" грн\n\n");

        //Додаткова статистика
        List<Rental> rentals = rentalService.findAllRentals();
        if (!rentals.isEmpty()) {
            double minPrice = rentals.stream().mapToDouble(Rental::getTotalPrice).min().orElse(0);
            double maxPrice = rentals.stream().mapToDouble(Rental::getTotalPrice).max().orElse(0);

            sb.append("Мінімальна вартість: ").append(String.format("%.2f", minPrice)).append(" грн\n");
            sb.append("Максимальна вартість: ").append(String.format("%.2f", maxPrice)).append(" грн\n");
            sb.append("Всього оренд: ").append(rentals.size());
        }

        //Вивід
        showOutput(sb.toString());
    }

    //Автівка з найбільшим пробігом
    private void showCarWithHighestMileage() {
        Car car = rentalService.carWithHighestMileage();

        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗВІТ: АВТОМОБІЛЬ З НАЙБІЛЬШИМ ПРОБІГОМ ===\n\n");

        if (car != null) {
            sb.append(formatCarInfo(car));

            //Додавання інформації про оренди авто
            List<Rental> carRentals = rentalService.findAllRentalsByCarId(car.getId());
            if (!carRentals.isEmpty()) {
                sb.append("\n\nІсторія оренд:\n");
                for (int i = 0; i < carRentals.size(); i++) {
                    Rental rental = carRentals.get(i);
                    sb.append("[").append(i + 1).append("] ").append(formatShortRentalInfo(rental));
                    sb.append("\n");
                }
                sb.append("\nВсього оренд: ").append(carRentals.size());
            }
        } else {
            sb.append("Не вдалося знайти автомобіль з пробігом.");
        }

        //Вивід
        showOutput(sb.toString());
    }

    //Інформація про клієнта з найбільшою кількістю оренд
    private void showClientWithMostRentals() {
        Client client = rentalService.clientWithMostRentals();

        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗВІТ: КЛІЄНТ З НАЙБІЛЬШОЮ КІЛЬКІСТЮ ОРЕНД ===\n\n");

        if (client != null) {
            sb.append(formatClientInfo(client));

            //Додавання інформації про оренди клієнта
            List<Rental> clientRentals = rentalService.findAllRentalsByClientId(client.getId());
            if (!clientRentals.isEmpty()) {
                sb.append("\n\nІсторія оренд:\n");
                for (int i = 0; i < clientRentals.size(); i++) {
                    Rental rental = clientRentals.get(i);
                    sb.append("[").append(i + 1).append("] ").append(formatShortRentalInfo(rental));
                    sb.append("\n");
                }
            }
        } else {
            sb.append("Не вдалося знайти клієнта з орендами.");
        }

        //Вивід
        showOutput(sb.toString());
    }

    //Найдовша оренда
    private void showLongestRental() {
        Rental rental = rentalService.longestRental();

        StringBuilder sb = new StringBuilder();
        sb.append("=== ЗВІТ: НАЙТРИВАЛІША ОРЕНДА ===\n\n");

        if (rental != null) {
            sb.append(formatRentalInfo(rental));
        } else {
            sb.append("Не вдалося знайти оренду.");
        }

        //Вивід
        showOutput(sb.toString());
    }

    //Інформація про усі оренди, автівки та клієнтів
    private void showAllData() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ПОВНИЙ ЗВІТ ПО СИСТЕМІ ===\n\n");

        //Клієнти
        List<Client> clients = rentalService.findAllClients();
        sb.append("=== КЛІЄНТИ ===\n");
        sb.append("Всього: ").append(clients.size()).append("\n\n");
        for (int i = 0; i < Math.min(clients.size(), 5); i++) {
            sb.append("[").append(i + 1).append("] ").append(formatShortClientInfo(clients.get(i))).append("\n");
        }
        if (clients.size() > 5) {
            sb.append("... і ще ").append(clients.size() - 5).append(" клієнтів\n");
        }

        //Автомобілі
        List<Car> cars = rentalService.findAllCars();
        sb.append("\n=== АВТОМОБІЛІ ===\n");
        sb.append("Всього: ").append(cars.size()).append("\n\n");
        for (int i = 0; i < Math.min(cars.size(), 5); i++) {
            sb.append("[").append(i + 1).append("] ").append(formatShortCarInfo(cars.get(i))).append("\n");
        }
        if (cars.size() > 5) {
            sb.append("... і ще ").append(cars.size() - 5).append(" автомобілів\n");
        }

        //Оренди
        List<Rental> rentals = rentalService.findAllRentals();
        sb.append("\n=== ОРЕНДИ ===\n");
        sb.append("Всього: ").append(rentals.size()).append("\n");
        sb.append("Активних: ").append(rentals.stream().filter(Rental::isActive).count()).append("\n");
        sb.append("Завершених/запланованих: ").append(rentals.stream().filter(r -> !r.isActive()).count()).append("\n");

        showOutput(sb.toString());
    }

    //Інформація про заробіток
    private void showFinancialReport() {
        List<Rental> rentals = rentalService.findAllRentals();

        StringBuilder sb = new StringBuilder();
        sb.append("=== ФІНАНСОВИЙ ЗВІТ ===\n\n");

        double totalRevenue = rentals.stream().mapToDouble(Rental::getTotalPrice).sum();
        double avgPrice = rentals.isEmpty() ? 0 : totalRevenue / rentals.size();
        long activeRentals = rentals.stream().filter(Rental::isActive).count();

        sb.append("Загальний дохід: ").append(String.format("%.2f", totalRevenue)).append(" грн\n");
        sb.append("Середній чек: ").append(String.format("%.2f", avgPrice)).append(" грн\n");
        sb.append("Всього оренд: ").append(rentals.size()).append("\n");
        sb.append("Активних оренд: ").append(activeRentals).append("\n");
        sb.append("Завершених/запланованих оренд: ").append(rentals.size() - activeRentals).append("\n");

        showOutput(sb.toString());
    }

    //Інформація про усі активні (насьогодні) оренди
    private void showActiveRentals() {
        List<Rental> rentals = rentalService.findAllRentals();

        StringBuilder sb = new StringBuilder();
        sb.append("=== АКТИВНІ ОРЕНДИ ===\n\n");

        long activeCount = rentals.stream().filter(Rental::isActive).count();
        sb.append("Всього активних оренд: ").append(activeCount).append("\n\n");

        int count = 0;
        for (Rental rental : rentals) {
            if (rental.isActive()) {
                count++;
                sb.append("[").append(count).append("] ").append(formatRentalInfo(rental));
                sb.append("\n").append("-".repeat(50)).append("\n");
            }
        }

        if (count == 0) {
            sb.append("Активних оренд не знайдено.");
        }

        showOutput(sb.toString());
    }

    //Допоміжні методи для форматування
    private String formatCarInfo(Car car) {
        return String.format("ID: %s\nМарка: %s\nМодель: %s\nРік: %d\nПробіг: %d км\nЦіна/день: %.2f грн",
                car.getId(), car.getBrand(), car.getModel(), car.getYear(), car.getMileage(), car.getPricePerDay());
    }

    private String formatShortCarInfo(Car car) {
        return String.format("%s %s (%d) - %.2f грн/день",
                car.getBrand(), car.getModel(), car.getYear(), car.getPricePerDay());
    }

    private String formatClientInfo(Client client) {
        return String.format("ID: %s\nІм'я: %s\nТелефон: %s\nКількість оренд: %d",
                client.getId(), client.getName(), client.getPhone(), client.getRentalCount());
    }

    private String formatShortClientInfo(Client client) {
        return String.format("%s (%s) - %d оренд",
                client.getName(), client.getPhone(), client.getRentalCount());
    }

    private String formatRentalInfo(Rental rental) {
        return String.format("ID: %s\nАвто: %s\nКлієнт: %s\nПеріод: %s - %s\nТривалість: %d дн.\nВартість: %.2f грн\nСтатус: %s",
                rental.getId(), rental.getCarId(), rental.getClientId(),
                rental.getStartDate(), rental.getEndDate(), rental.totalDays(),
                rental.getTotalPrice(), rental.isActive() ? "Активна" : "Завершена/запланована");
    }

    private String formatShortRentalInfo(Rental rental) {
        return String.format("%s - %s (%.2f грн)",
                rental.getStartDate(), rental.getEndDate(), rental.getTotalPrice());
    }

    private void showOutput(String text) {
        outputTextArea.setText(text);
    }
}