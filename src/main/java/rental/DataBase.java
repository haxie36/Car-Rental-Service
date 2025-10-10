package rental;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DataBase {
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
        //Створіть директорію, якщо її немає
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
}
