package rental;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalService {
    private final DataBase dataBase = DataBase.getInstance();

    public RentalService(){}

    public static boolean isValidCarId(String carId) {return carId != null && !carId.trim().isEmpty();}
    public static boolean isValidClientId(String clientId) {return clientId != null && !clientId.trim().isEmpty();}

    private String generateClientId() {
        int maxId = 0;
        for (Client client : dataBase.getClients()) {
            if (client.getId().startsWith("CLIENT")) {
                try {
                    int idNum = Integer.parseInt(client.getId().substring(6));
                    if (idNum > maxId) maxId = idNum;
                } catch (NumberFormatException e) {
                    //Ігноруємо неправильні ID
                }
            }
        }
        return "CLIENT" + (maxId + 1);
    }

    private String generateCarId() {
        int maxId = 0;
        for (Car car : dataBase.getCars()) {
            if (car.getId().startsWith("CAR")) {
                try {
                    int idNum = Integer.parseInt(car.getId().substring(3));
                    if (idNum > maxId) maxId = idNum;
                } catch (NumberFormatException e) {
                    //Ігноруємо неправильні ID
                }
            }
        }
        return "CAR" + (maxId + 1);
    }

    private String generateRentalId() {
        int maxId = 0;
        for (Rental rental : dataBase.getRentals()) {
            if (rental.getId().startsWith("RENT")) {
                try {
                    int idNum = Integer.parseInt(rental.getId().substring(4));
                    if (idNum > maxId) maxId = idNum;
                } catch (NumberFormatException e) {
                    //Ігноруємо неправильні ID
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
            //Перевірка на те, чи є дата між датою початку та кінця деякої оренди
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
    public void addClientDirect(String name, String phone) {
        String id = generateClientId();
        dataBase.addClient(new Client(id,name,phone));
    }

    //Додавання авто
    public void addCarDirect(String brand, String model, int year, int mileage, double pricePerDay) {
        String id = generateCarId();
        dataBase.addCar(new Car(id, brand, model, year, mileage, pricePerDay));
    }

    //Додавання оренди
    public boolean addRentalDirect(String carId, String clientId, LocalDate startDate, LocalDate endDate) {
        try {
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

            car.addRental(rental);
            rental.setCar(car);
            client.addRental(rental);
            rental.setClient(client);
            dataBase.addRental(rental);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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

    //Знайти доступні автомобілі на дати
    public List<Car> findAvailableCars(LocalDate startDate, LocalDate endDate) {
        return dataBase.findAvailableCars(startDate, endDate);
    }

    //CRUD методи з DataBase

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

    //Пошук всіх оренд клієнта за ID
    public List<Rental> findAllRentalsByClientId(String clientId) {
        return dataBase.findAllRentalsByClientId(clientId);
    }

    //Пошук всіх оренд автівки за ID
    public List<Rental> findAllRentalsByCarId(String carId) {
        return dataBase.findAllRentalsByCarId(carId);
    }
    public void removeAllRentalsOfClient(String id){
        dataBase.removeAllRentalsOfClient(id);
    }
    public void removeAllCarRentals(String carId) {
        dataBase.removeAllCarRentals(carId);
    }

    public void reloadData(){dataBase.loadData();}
}
