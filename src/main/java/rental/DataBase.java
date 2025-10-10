package rental;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
        //Завантаження клієнтів
        ClientRepository.loadData();
        //Завантаження автівок
        CarRepository.loadData();
        //Завантаження оренд
        RentlaRepository.loadData();

        //І, нарешті, синхронізація посилань
        syncReferences();
    }

    //Синхронізація посилань між класами
    public void syncReferences(){
        //Очищення посилань для всіх клієнтів та авто
        for (Client client : getClients()) {
            client.getRentals().clear();
            client.getRentalIds().clear();
        }
        for (Car car : getCars()) {
            car.setRentals(new ArrayList<>());
        }

        //Основна частина
        for (Rental rental : RentlaRepository.getRentals()) {
            String clientId = rental.getClientId();
            String carId = rental.getCarId();

            //Відновлення посилання на клієнта
            if (clientId != null) {
                for (Client client : ClientRepository.getClients()) {
                    if (clientId.equals(client.getId())) {
                        rental.setClient(client);
                        break;
                    }
                }
            }

            //Відновлення посилання на авто
            if (carId != null) {
                for (Car car : CarRepository.getCars()) {
                    if (carId.equals(car.getId())) {
                        rental.setCar(car);
                        break;
                    }
                }
            }
        }

        //Відновлення посилань з клієнтів та авто на оренди
        for (Client client : ClientRepository.getClients()) {
            client.updateRentalReferences(RentlaRepository.getRentals());
        }

        for (Car car : CarRepository.getCars()) {
            List<Rental> carRentals = new ArrayList<>();
            for (Rental rental : RentlaRepository.getRentals()) {
                if (car.getId().equals(rental.getCarId())) {
                    carRentals.add(rental);
                }
            }
            car.setRentals(carRentals);
        }

        System.out.println("Синхронізація посилань проведена успішно!");
    }

    //Збереження
    public void saveData(){
        //Збереження клієнтів
        ClientRepository.saveData();
        //Збереження автівок
        CarRepository.saveData();
        //Збереження оренд
        RentlaRepository.saveData();
    }

    public static List<Client> getClients(){return ClientRepository.getClients();}
    public static List<Car> getCars(){return CarRepository.getCars();}
    public static List<Rental> getRentals(){return RentlaRepository.getRentals();}

    public boolean isDataSetEmpty(){return getClients().isEmpty() ||getCars().isEmpty() || getRentals().isEmpty();}
}
