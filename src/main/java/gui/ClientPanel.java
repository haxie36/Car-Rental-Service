package gui;

import rental.Client;
import rental.Rental;
import rental.RentalService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ClientPanel extends JPanel {
    private final RentalService rentalService;
    private JTextArea outputTextArea;
    private JTextField idField, nameField, phoneField;

    public ClientPanel(RentalService rentalService) {
        this.rentalService = rentalService;
        initialize();
    }

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

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Керування клієнтами"));

        //Кнопки управління
        JButton addClientBtn = createStyledButton("Додати клієнта");
        JButton removeClientBtn = createStyledButton("Видалити клієнта");
        JButton findClientByIdBtn = createStyledButton("Пошук клієнта за ID");
        JButton findClientByNamePhoneBtn = createStyledButton("Пошук за іменем та телефоном");
        JButton listClientsBtn = createStyledButton("Список всіх клієнтів");
        JButton showClientRentalsBtn = createStyledButton("Оренди клієнта");

        //Поля для вводу
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Поля для пошуку"));
        inputPanel.setMaximumSize(new Dimension(250, 100));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("ПІБ:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Телефон:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);

        //Додавання елементів з відступами
        panel.add(addClientBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(removeClientBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(findClientByIdBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(findClientByNamePhoneBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(listClientsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(showClientRentalsBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(inputPanel);

        //Обробники подій
        addClientBtn.addActionListener(e -> showAddClientDialog());
        removeClientBtn.addActionListener(e -> removeClient());
        findClientByIdBtn.addActionListener(e -> findClientById());
        findClientByNamePhoneBtn.addActionListener(e -> findClientByNamePhone());
        listClientsBtn.addActionListener(e -> listAllClients());
        showClientRentalsBtn.addActionListener(e -> showClientRentals());

        panel.setPreferredSize(new Dimension(250, 600));
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 30));
        button.setPreferredSize(new Dimension(200, 30));
        return button;
    }

    private void showAddClientDialog() {
        AddClientDialog dialog = new AddClientDialog(rentalService);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            showOutput("Клієнта успішно додано!\n\n" + getLastClientInfo());
        }
    }

    private String getLastClientInfo() {
        List<Client> clients = rentalService.findAllClients();
        if (!clients.isEmpty()) {
            Client lastClient = clients.getLast();
            return formatClientInfo(lastClient);
        }
        return "Інформація недоступна";
    }

    private void removeClient() {
        String clientId = idField.getText().trim();
        if (clientId.isEmpty()) {
            showOutput("Помилка: Введіть ID клієнта!");
            return;
        }

        try {
            rentalService.removeClient(clientId);
            showOutput("Клієнта успішно видалено!");
            clearFields();
        } catch (Exception ex) {
            showOutput("Помилка: " + ex.getMessage());
        }
    }

    private void findClientById() {
        String clientId = idField.getText().trim();
        if (clientId.isEmpty()) {
            showOutput("Помилка: Введіть ID клієнта!");
            return;
        }

        Client client = rentalService.findClient(clientId);
        if (client != null) {
            showOutput("Знайдено клієнта:\n" + formatClientInfo(client));
        } else {
            showOutput("Клієнта з ID " + clientId + " не знайдено.");
        }
    }

    private void findClientByNamePhone() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            showOutput("Помилка: Введіть ім'я та телефон!");
            return;
        }

        Client client = rentalService.findClient(name, phone);
        if (client != null) {
            showOutput("Знайдено клієнта:\n" + formatClientInfo(client));
        } else {
            showOutput("Клієнта з іменем '" + name + "' та телефоном '" + phone + "' не знайдено.");
        }
    }

    private void listAllClients() {
        List<Client> clients = rentalService.findAllClients();
        if (clients != null && !clients.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("=== ВСІ КЛІЄНТИ ===\n\n");
            for (int i = 0; i < clients.size(); i++) {
                Client client = clients.get(i);
                sb.append("[").append(i + 1).append("] ").append(formatClientInfo(client));
                sb.append("\n").append("-".repeat(50)).append("\n");
            }
            sb.append("\nВсього клієнтів: ").append(clients.size());
            showOutput(sb.toString());
        } else {
            showOutput("Клієнтів не знайдено.");
        }
    }

    private void showClientRentals() {
        String clientId = idField.getText().trim();
        if (clientId.isEmpty()) {
            showOutput("Помилка: Введіть ID клієнта!");
            return;
        }

        Client client = rentalService.findClient(clientId);
        if (client != null) {
            List<Rental> rentals = rentalService.findAllRentalsByClientId(clientId);
            displayClientRentals(client, rentals);
        } else {
            showOutput("Клієнта з ID " + clientId + " не знайдено.");
        }
    }

    private void displayClientRentals(Client client, List<Rental> rentals) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ІНФОРМАЦІЯ ПРО КЛІЄНТА ===\n");
        sb.append(formatClientInfo(client));
        sb.append("\n\n=== ОРЕНДИ КЛІЄНТА ===\n\n");

        if (rentals != null && !rentals.isEmpty()) {
            for (int i = 0; i < rentals.size(); i++) {
                Rental rental = rentals.get(i);
                sb.append("[").append(i + 1).append("] ").append(formatRentalInfo(rental));
                sb.append("\n").append("-".repeat(50)).append("\n");
            }
            sb.append("\nВсього оренд: ").append(rentals.size());
        } else {
            sb.append("Клієнт не має оренд.");
        }

        showOutput(sb.toString());
    }

    private String formatClientInfo(Client client) {
        return "ID: " + client.getId() + "\n" +
                "Ім'я: " + client.getName() + "\n" +
                "Телефон: " + client.getPhone() + "\n" +
                "Кількість оренд: " + client.getRentalCount() + "\n" +
                "Активні оренди: " + (client.hasActiveRentals() ? "Так" : "Ні");
    }

    private String formatRentalInfo(Rental rental) {
        return "ID оренди: " + rental.getId() + "\n" +
                "Автомобіль: " + rental.getCarId() + "\n" +
                "Період: " + rental.getStartDate() + " - " + rental.getEndDate() + "\n" +
                "Тривалість: " + rental.totalDays() + " дн.\n" +
                "Вартість: " + String.format("%.2f", rental.getTotalPrice()) + " грн\n" +
                "Статус: " + (rental.isActive() ? "Активна" : "Завершена/запланована");
    }

    private void showOutput(String text) {
        outputTextArea.setText(text);
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
    }
}