package rental;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalService {
    final static String toEndItAll ="(Якщо ви бажаєте зупинити процес вводу, нічого не вводьте та нажміть Enter)";
    private final DataBase dataBase = DataBase.getInstance();

    public RentalService(){}

    private boolean isValidCarId(String carId) {return carId != null && !carId.trim().isEmpty();}
    private boolean isValidClientId(String clientId) {return clientId != null && !clientId.trim().isEmpty();}

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


    //Додавання користувача
    public void addClient(Client client){
        dataBase.getClients().add(client);
        dataBase.saveData();
    }

    //Додавання автівки
    public void addCar(Car car){

        dataBase.getCars().add(car);
        System.out.println("Успішно додано автівку!");
        dataBase.saveData();
    }

    //Додавання оренди
    public void addRental(Rental rental){
        dataBase.getRentals().add(rental);
        System.out.println("Успішно додано оренду!");
        dataBase.saveData();
    }

    //Пошук користувачів різними методами
    public Client findClient(String clientId){
        //Перевірка на наявність даних
        if (dataBase.getClients().isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return null;
        }
        //Перевірку на null
        if (!isValidClientId(clientId)) {
            System.out.println("ID користувача не може бути пустим!");
            return null;
        }

        //Основна частина
        Client foundClient = null;
        for (Client client : dataBase.getClients()) {
            if (clientId.equals(client.getId())) {
                foundClient = client;
                break;
            }
        }
        return foundClient;
    }
    public Client findClient(String name,String phone){
        //Перевірка на наявність даних
        if (dataBase.getClients().isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return null;
        }

        Client foundClient=null;
        for (Client client : dataBase.getClients()) {
            if (client.getName().equals(name) && client.getPhone().equals(phone)) {
                foundClient = client;
                break;
            }
        }
        return foundClient;
    }

    //Видалення користувача різними методами
    public void removeClient(Client client){
        //Перевірка на наявність даних
        if (dataBase.getClients().isEmpty()) {
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

        //Знаходимо всі оренди цього користувача
        List<Rental> rentalsToRemove = new ArrayList<>();
        for (Rental rental : dataBase.getRentals()) {
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
        dataBase.getRentals().removeAll(rentalsToRemove);

        //Видаляємо користувача
        dataBase.getClients().remove(client);
        dataBase.saveData();
    }
    public void removeClient(String id){
        //Перевірка на наявність даних
        if (dataBase.getClients().isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return;
        }

        Client client = findClient(id);
        removeClient(client);
    }

    //Пошук автівки
    public Car findCar(String id){
        //Перевірка на наявність даних
        if (dataBase.getCars().isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return null;
        }

        //Основна частина
        for (Car car : dataBase.getCars()) {
            if (id.equals(car.getId())) {
                return car;
            }
        }
        System.out.println("Автівку не знайдено!");
        return null;
    }

    //Видалення автівки різними методами
    public void removeCar(Car car){
        //Перевірка на наявність даних
        if (car==null||dataBase.getCars().isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return;
        }

        //Основна частина
        //Знаходимо всі оренди цієї машини
        List<Rental> carRentals = new ArrayList<>();
        for (Rental rental : dataBase.getRentals()) {
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
            dataBase.getRentals().remove(rental);
        }

        //Видалення машини
        dataBase.getCars().remove(car);
        dataBase.saveData();
    }
    public void removeCar(String id){
        Car car = findCar(id);
        removeCar(car);
    }

    //Пошук оренди різними методами
    public Rental findRental(String id){
        //Перевірка на наявність даних
        if (dataBase.getRentals().isEmpty()) {
            System.out.println("Немає з чім працювати!");
            return null;
        }

        //Основна частина
        for (Rental rental : dataBase.getRentals()) {
            if (rental.getId().equals(id)) {
                return rental;
            }
        }
        return null;
    }
    //Пошук всіх оренд користувача за ID
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

    //Видалення оренди різними методами
    public void removeRental(Rental rental){
        //Перевірка на наявність даних
        if (dataBase.isDataSetEmpty()){
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
        dataBase.getRentals().remove(rental);
        System.out.println("Успішно видалено оренду!");
        dataBase.saveData();
    }
    public void removeRental(String id){
        //Перевірка на наявність даних
        if (dataBase.isDataSetEmpty()){
            System.out.println("Немає з чім працювати!");
            return;
        }

        Rental rental = findRental(id);
        removeRental(rental);
    }

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
    public void removeAllCarRentals(String carId){
        Car car = findCar(carId);
        if (car==null) {return;}

        List<Rental> carRentals = new ArrayList<>(findAllRentalsByCarId(carId));

        for (Rental rental : carRentals) {
            if (rental.getClient() != null) {
                rental.getClient().removeRental(rental);
            }
            dataBase.getRentals().remove(rental);
        }
        car.getRentals().clear();
        dataBase.saveData();
    }

    //Звітування
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
        Rental longest = dataBase.getRentals().get(0);
        for (Rental rental : dataBase.getRentals()) {
            if (rental.totalDays() > longest.totalDays()) {
                longest = rental;
            }
        }
        return longest;
    }

    //Для GUI
    //Додавання клієнта без консольного вводу
    public void addClientDirect(String name, String phone) {
        String id = generateClientId();
        addClient(new Client(id,name,phone));
    }

    //Додавання авто без консольного вводу
    public void addCarDirect(String brand, String model, int year, int mileage, double pricePerDay) {
        String id = generateCarId();
        addCar(new Car(id, brand, model, year, mileage, pricePerDay));
    }

    //Додавання оренди без консольного вводу
    public boolean addRentalDirect(String carId, String clientId, LocalDate startDate, LocalDate endDate) {
        try {
            Car car = findCar(carId);
            Client client = findClient(clientId);

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
            addRental(rental);
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
        List<Car> availableCars = new ArrayList<>();
        for (Car car : dataBase.getCars()) {
            if (car.isAvailable(startDate, endDate)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }
}
