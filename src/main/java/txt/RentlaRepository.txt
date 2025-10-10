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

public final class RentlaRepository{
    private static List<Rental> rentals= new ArrayList<>();
    private final static ObjectMapper mapper = new ObjectMapper() {{
        enable(SerializationFeature.INDENT_OUTPUT); //Додає перенесення рядків та відступи
        registerModule(new JavaTimeModule()); //Для роботи з LocalDate
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }};

    //Завантаження "БД" з Json
    public static void loadData() {
        try {
            File RentalsJson = new File(Path.RENTALS.getPath());
            if (RentalsJson.exists()) {
                rentals = mapper.readValue(RentalsJson, new TypeReference<>() {
                });
                System.out.println("Оренди успішно завантажені (" + rentals.size() + ")");
            } else {
                System.out.println("file doesn't exist");
            }
        } catch (IOException e) {
            System.out.println("Критична помилка при завантаженні даних: " + e.getMessage());
            System.exit(0);
        }
    }

    //Збереження
    public static void saveData(){
        try{
            mapper.writeValue(new File(Path.RENTALS.getPath()), rentals);
            System.out.println("Вдале збереження оренд!");
        } catch (IOException e){
            System.out.println("Помилка збереження оренд: " + e.getMessage());
        }

    }

    public static List<Rental> getRentals() {return rentals;}
}
