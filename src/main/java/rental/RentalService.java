package rental;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalService {
    private final DataBase dataBase = DataBase.getInstance();

    public RentalService(){}

    //Перевірка на валідність Id
    public static boolean isValidCarId(String carId) {return carId != null && !carId.trim().isEmpty();}
    public static boolean isValidClientId(String clientId) {return clientId != null && !clientId.trim().isEmpty();}

    //Створення Id клієнта
    private String generateClientId() {
        int maxId = 0;
        for (Client client : dataBase.getClients()) {
            if (client.getId().startsWith("CLIENT")) {
                try { //Отримання найбільшого номера Id
                    int idNum = Integer.parseInt(client.getId().substring(6));
                    if (idNum > maxId) maxId = idNum;
                } catch (NumberFormatException e) {
                    //Ігноруємо неправильні Id
                }
            }
        }
        return "CLIENT" + (maxId + 1);
    }

    //Створення Id автівки
    private String generateCarId() {
        int maxId = 0;
        for (Car car : dataBase.getCars()) {
            if (car.getId().startsWith("CAR")) {
                try { //Отримання найбільшого номера Id
                    int idNum = Integer.parseInt(car.getId().substring(3));
                    if (idNum > maxId) maxId = idNum;
                } catch (NumberFormatException e) {
                    //Ігноруємо неправильні Id
                }
            }
        }
        return "CAR" + (maxId + 1);
    }

    //Створення Id оренди
    private String generateRentalId() {
        int maxId = 0;
        for (Rental rental : dataBase.getRentals()) {
            if (rental.getId().startsWith("RENT")) {
                try { //Отримання найбільшого номера Id
                    int idNum = Integer.parseInt(rental.getId().substring(4));
                    if (idNum > maxId) maxId = idNum;
                } catch (NumberFormatException e) {
                    //Ігноруємо неправильні Id
                }
            }
        }
        return "RENT" + (maxId + 1);
    }

    //================================ЗВіТИ===================================

    public int rentalsOn(LocalDate date){
        int count=0;
        //Перевірка на наявність даних
        if (dataBase.getRentals().isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return 0;
        }

        for (Rental rental : dataBase.getRentals()) {
            LocalDate startDate = rental.getStartDate();
            LocalDate endDate = rental.getEndDate();
            //Перевірка на те, чи належить вхідна дата проміжку активності деякої оренди
            if ((date.equals(startDate) || date.isAfter(startDate)) && (date.equals(endDate) || date.isBefore(endDate))) {
                count++;
            }
        }
        return count;
    }

    public double averageRentalPrice(){
        //Перевірка на наявність даних
        if (dataBase.getRentals().isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return 0;
        }

        //Основна частина
        double average;
        double sum = 0;
        for (Rental rental : dataBase.getRentals()) {
            sum += rental.getTotalPrice();
        }
        average = sum / dataBase.getRentals().size();
        return average;
    }

    public Car carWithHighestMileage() {
        //Перевірка на наявність даних
        if (dataBase.getCars().isEmpty()) {
            System.out.println("Немає з чим працювати!");
            return null;
        }
        
        //Основна частина
        Car theCar = null;
        int highestMileage = -1;

        for (Car car : dataBase.getCars()) {
            if (car.getMileage() > highestMileage) {
                theCar = car;
                highestMileage = car.getMileage();
            }
        }
        return theCar;
    }

    public Client clientWithMostRentals(){
        //Перевірка на наявність даних
        if (dataBase.getClients().isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return null;
        }

        //Основна частина
        Client theClient=null;
        int rentalsCount = -1;

        for (Client client : dataBase.getClients()) {
            if (client.getRentalCount() > rentalsCount) {
                rentalsCount = client.getRentalCount();
                theClient = client;
            }
        }
        return theClient;
    }

    public Rental longestRental(){
        //Перевірка на наявність даних
        if (dataBase.getRentals().isEmpty()) {
            System.out.println("Немає з чим працювати!");
            return null;
        }

        //Основна частина
        Rental longest = dataBase.getRentals().getFirst();
        for (Rental rental : dataBase.getRentals()) {
            if (rental.totalDays() > longest.totalDays()) {
                longest = rental;
            }
        }
        return longest;
    }

    //====================================ДЛЯ GUI==================================
    //Додавання клієнта
    public boolean addClientDirect(String name, String phone) {
        String id = generateClientId();
        if (dataBase.findClient(name, phone) != null) { return false; }
        dataBase.addClient(new Client(id,name,phone));
        return true;
    }

    //Додавання авто
    public void addCarDirect(String brand, String model, int year, int mileage, double pricePerDay) {
        String id = generateCarId();
        dataBase.addCar(new Car(id, brand, model, year, mileage, pricePerDay));
    }

    //Додавання оренди
    public boolean addRentalDirect(String carId, String clientId, LocalDate startDate, LocalDate endDate) {
        try {
            //Отримання автівки та клієнта за Id
            Car car = dataBase.findCar(carId);
            Client client = dataBase.findClient(clientId);

            if (car == null || client == null) {
                return false;
            }
            if (!car.isAvailable(startDate, endDate)) {
                return false;
            }

            String id = generateRentalId();
            Rental rental = new Rental(id, carId, clientId, startDate, endDate);
            rental.setTotalPrice(car.getPricePerDay()*rental.totalDays());

            //Додавання посилань між об'єктами
            car.addRental(rental);
            rental.setCar(car);
            client.addRental(rental);
            rental.setClient(client);

            //Додавання оренди до бази даних
            dataBase.addRental(rental);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Знайти доступні автомобілі на дати
    public List<Car> findAvailableCars(LocalDate startDate, LocalDate endDate) {
        return dataBase.findAvailableCars(startDate, endDate);
    }

    //Пошук всіх оренд клієнта за ID
    public List<Rental> findAllRentalsByClientId(String clientId) {
        if (dataBase.getRentals().isEmpty()) {
            System.out.println("Немає оренд!");
            return new ArrayList<>();
        }

        return findClient(clientId).getRentals();
    }

    //Пошук всіх оренд автівки за ID
    public List<Rental> findAllRentalsByCarId(String carId) {
        //Перевірка на наявність даних
        if (dataBase.getRentals().isEmpty()) {
            System.out.println("Немає оренд!");
            return new ArrayList<>();
        }

        //Основна частина
        List<Rental> carRentals = new ArrayList<>();
        for (Rental rental : dataBase.getRentals()) {
            if (rental.getCarId().equals(carId)) {
                carRentals.add(rental);
            }
        }
        return carRentals;
    }

    //Видалення всіх оренд клієнта за ID
    public void removeAllRentalsOfClient(String id){
        Client client = findClient(id);
        if (client == null) {return;}
        List<Rental> clientRentals = client.getRentals();
        for (Rental rental : clientRentals) {
            rental.getCar().removeRental(rental);
        }
        client.getRentals().clear();
        client.getRentalIds().clear();
        dataBase.getRentals().removeAll(clientRentals);
        dataBase.saveData();
    }

    //Видалення всіх оренд автівки за ID
    public void removeAllCarRentals(String carId) {
        Car car = findCar(carId);
        if (car == null) {
            return;
        }

        List<Rental> carRentals = new ArrayList<>(findAllRentalsByCarId(carId));

        for (Rental rental : carRentals) {
            if (rental.getClient() != null) {
                rental.getClient().removeRental(rental);
            }
            dataBase.removeRental(rental);
        }
        car.getRentals().clear();
        dataBase.saveData();
    }

    //CRUD методи з DataBase

    //Отримати всіх автівок
    public List<Car> findAllCars() {
        return new ArrayList<>(dataBase.getCars());
    }

    //Отримати всіх клієнтів
    public List<Client> findAllClients() {
        return new ArrayList<>(dataBase.getClients());
    }

    //Отримати всі оренди
    public List<Rental> findAllRentals() {
        return new ArrayList<>(dataBase.getRentals());
    }

    //Пошук користувачів різними методами
    public Client findClient(String clientId){
        return dataBase.findClient(clientId);
    }

    public Client findClient(String name,String phone){
        return dataBase.findClient(name,phone);
    }

    //Пошук автівки
    public Car findCar(String id){
        return dataBase.findCar(id);
    }

    //Пошук оренди
    public Rental findRental(String id){
        return dataBase.findRental(id);
    }

    //Видалення клієнта
    public void removeClient(String id){
        dataBase.removeClient(id);
    }

    //Видалення автівки
    public void removeCar(String id){
        dataBase.removeCar(id);
    }

    //Видалення оренди
    public void removeRental(String id){
        dataBase.removeRental(id);
    }

    public void reloadData(){dataBase.loadData();}
}
