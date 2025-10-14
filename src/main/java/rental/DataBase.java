package rental;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

final class DataBase {
    private static DataBase instance; //Singleton pattern
    private final ObjectMapper mapper = new ObjectMapper() {{
        enable(SerializationFeature.INDENT_OUTPUT); //Додає перенесення рядків та відступи
        registerModule(new JavaTimeModule()); //Для роботи з LocalDate
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }};

    private List<Client> clients= new ArrayList<>();
    private List<Car> cars= new ArrayList<>();
    private List<Rental> rentals= new ArrayList<>();

    //Singleton конструктор
    private DataBase() {
        loadData();
    }

    //Singleton метод
    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    //Завантаження "БД" з Json
    public void loadData(){
        try {
            //Завантаження користувачів
            File ClientJson = new File(Path.CLIENTS.getPath());
            if (ClientJson.exists()){
                clients = mapper.readValue(ClientJson, new TypeReference<>() {});
                System.out.println("Користувачі успішно завантажені ("+clients.size()+")");
            }else {
                System.out.println("file doesn't exist");
            }

            //Завантаження автівок
            File CarJson = new File(Path.CARS.getPath());
            if (CarJson.exists()){
                cars = mapper.readValue(CarJson, new TypeReference<>() {});
                System.out.println("Автівки успішно завантажені ("+cars.size()+")");
            }else {
                System.out.println("file doesn't exist");
            }

            //Завантаження оренд
            File RentalsJson = new File(Path.RENTALS.getPath());
            if (RentalsJson.exists()){
                rentals = mapper.readValue(RentalsJson, new TypeReference<>() {
                });
                System.out.println("Оренди успішно завантажені ("+rentals.size()+")");
            }else {
                System.out.println("file doesn't exist");
            }
        } catch (IOException e) {
            System.out.println("Критична помилка при завантаженні даних: " + e.getMessage());
            System.exit(0);
        }

        //І, нарешті, синхронізація посилань
        syncReferences();
    }

    //Синхронізація посилань між класами
    public void syncReferences(){
        //Очищення посилань для всіх клієнтів та авто
        for (Client client : clients) {
            client.getRentals().clear();
            client.getRentalIds().clear();
        }
        for (Car car : cars) {
            car.setRentals(new ArrayList<>());
        }

        //Основна частина
        for (Rental rental : rentals) {
            String clientId = rental.getClientId();
            String carId = rental.getCarId();

            //Відновлення посилання на клієнта
            if (clientId != null) {
                for (Client client : clients) {
                    if (clientId.equals(client.getId())) {
                        rental.setClient(client);
                        break;
                    }
                }
            }

            //Відновлення посилання на авто
            if (carId != null) {
                for (Car car : cars) {
                    if (carId.equals(car.getId())) {
                        rental.setCar(car);
                        break;
                    }
                }
            }
        }

        //Відновлення посилань з клієнтів та авто на оренди
        for (Client client : clients) {
            client.updateRentalReferences(rentals);
        }

        for (Car car : cars) {
            List<Rental> carRentals = new ArrayList<>();
            for (Rental rental : rentals) {
                if (car.getId().equals(rental.getCarId())) {
                    carRentals.add(rental);
                }
            }
            car.setRentals(carRentals);
        }

        System.out.println("Синхронізація посилань проведена успішно!");
    }

    //Збереження відповідно
    public void saveData(){
        //Створить директорію, якщо її немає
        new File("src/data").mkdirs();

        //Збереження користувачів
        try{
            mapper.writeValue(new File(Path.CLIENTS.getPath()), clients);
            System.out.println("Вдале збереження користувачів!");
        } catch (IOException e){
            System.out.println("Помилка збереження користувачів: " + e.getMessage());
        }

        //Збереження автівок
        try{
            mapper.writeValue(new File(Path.CARS.getPath()), cars);
            System.out.println("Вдале збереження автівок!");
        } catch (IOException e){
            System.out.println("Помилка збереження автівок: " + e.getMessage());
        }

        //Збереження оренд
        try{
            mapper.writeValue(new File(Path.RENTALS.getPath()), rentals);
            System.out.println("Вдале збереження оренд!");
        } catch (IOException e){
            System.out.println("Помилка збереження оренд: " + e.getMessage());
        }

    }

    public List<Client> getClients(){return clients;}
    public List<Car> getCars(){return cars;}
    public List<Rental> getRentals(){return rentals;}

    public boolean isDataSetEmpty(){return clients.isEmpty() || cars.isEmpty() || rentals.isEmpty();}

    
    //==========================================ДОДАВАННЯ==============================================
    
    //Додавання клієнта
    public void addClient(Client client){
        clients.add(client);
        saveData();
    }

    //Додавання автівки
    public void addCar(Car car){
        cars.add(car);
        saveData();
    }

    //Додавання оренди
    public void addRental(Rental rental){
        rentals.add(rental);
        saveData();
    }
    
    //==============================================ПОШУК===============================================

    //Пошук користувачів різними методами
    public Client findClient(String clientId){
        //Перевірка на наявність даних
        if (clients.isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return null;
        }
        //Перевірку на null
        if (!RentalService.isValidClientId(clientId)) {
            System.out.println("ID клієнта не може бути пустим!");
            return null;
        }

        //Основна частина
        Client foundClient = null;
        for (Client client : clients) {
            if (clientId.equals(client.getId())) {
                foundClient = client;
                break;
            }
        }
        return foundClient;
    }

    public Client findClient(String name,String phone){
        //Перевірка на наявність даних
        if (clients.isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return null;
        }

        Client foundClient=null;
        for (Client client : clients) {
            if (client.getName().equals(name) && client.getPhone().equals(phone)) {
                foundClient = client;
                break;
            }
        }
        return foundClient;
    }


    //Пошук автівки
    public Car findCar(String id){
        //Перевірка на наявність даних
        if (cars.isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return null;
        }

        if (!RentalService.isValidCarId(id)) {
            System.out.println("ID автівки не може бути пустим!");
            return null;
        }

        //Основна частина
        for (Car car : cars) {
            if (id.equals(car.getId())) {
                return car;
            }
        }
        System.out.println("Автівку не знайдено!");
        return null;
    }
    
    //Пошук оренди
    public Rental findRental(String id){
        //Перевірка на наявність даних
        if (getRentals().isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return null;
        }

        //Основна частина
        for (Rental rental : getRentals()) {
            if (rental.getId().equals(id)) {
                return rental;
            }
        }
        return null;
    }

    //Знайти доступні автомобілі на дати
    public List<Car> findAvailableCars(LocalDate startDate, LocalDate endDate) {
        List<Car> availableCars = new ArrayList<>();
        for (Car car : cars) {
            if (car.isAvailable(startDate, endDate)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    //============================================ВИДАЛЕННЯ=====================================================

    //Видалення клієнта різними методами
    public void removeClient(Client client){
        //Перевірка на наявність даних
        if (clients.isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return;
        }
        //Перевірку на null
        if (client == null) {
            System.out.println("Користувач не може бути null!");
            return;
        }

        //Основна частина
        String clientId = client.getId();

        //Знаходимо всі оренди цього клієнта
        List<Rental> rentalsToRemove = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getClientId().equals(clientId)) {
                rentalsToRemove.add(rental);
            }
        }

        //Видаляємо посилання на оренди з машин
        for (Rental rental : rentalsToRemove) {
            if (rental.getCar() != null) {
                rental.getCar().removeRental(rental);
            }
        }

        //Видаляємо оренди з головного списку
        rentals.removeAll(rentalsToRemove);

        //Видаляємо клієнта
        clients.remove(client);
        saveData();
    }
    public void removeClient(String id){
        //Перевірка на наявність даних
        if (clients.isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return;
        }

        Client client = findClient(id);
        removeClient(client);
    }
    
    //Видалення автівки різними методами
    public void removeCar(Car car){
        //Перевірка на наявність даних
        if (car==null||cars.isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return;
        }

        //Основна частина
        //Знаходимо всі оренди цієї машини
        List<Rental> carRentals = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getCarId().equals(car.getId())) {
                carRentals.add(rental);
            }
        }

        //Видалення всіх пов'язаних оренд
        for (Rental rental : carRentals) {
            if (rental.getClient() != null) {
                rental.getClient().removeRental(rental);
            }
            //Видалення оренди
            rentals.remove(rental);
        }

        //Видалення машини
        cars.remove(car);
        saveData();
    }
    public void removeCar(String id){
        Car car = findCar(id);
        removeCar(car);
    }
    
    //Видалення оренди
    public void removeRental(Rental rental){
        //Перевірка на наявність даних
        if (isDataSetEmpty()){
            System.out.println("Немає з чім працювати!");
            return;
        }
        if (rental==null) {
            System.out.println("Оренду не знайдено!");
            return;
        }

        //Основна частина
        //Видалення посилання з машини
        if (rental.getCar() != null && rental.getCar().getRentals() != null) {
            rental.getCar().removeRental(rental);
        }

        //Видалення посилання з клієнта
        if (rental.getClient() != null) {
            rental.getClient().removeRental(rental);
        }

        //Видалення з головного списку
        getRentals().remove(rental);
        System.out.println("Успішно видалено оренду!");
        saveData();
    }

    public void removeRental(String id){
        //Перевірка на наявність даних
        if (isDataSetEmpty()){
            System.out.println("Немає з чім працювати!");
            return;
        }

        Rental rental = findRental(id);
        removeRental(rental);
    }
}
