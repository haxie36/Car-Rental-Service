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

public final class CarRepository {
    private static List<Car> cars= new ArrayList<>();
    private final static ObjectMapper mapper = new ObjectMapper() {{
        enable(SerializationFeature.INDENT_OUTPUT); //Додає перенесення рядків та відступи
        registerModule(new JavaTimeModule()); //Для роботи з LocalDate
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }};

    //Singleton конструктор
    private CarRepository() {}

    //Завантаження "БД" з Json
    public static void loadData() {
        try {
            File CarJson = new File(Path.CARS.getPath());
            if (CarJson.exists()) {
                cars = mapper.readValue(CarJson, new TypeReference<>() {
                });
                System.out.println("Автівки успішно завантажені (" + cars.size() + ")");
            } else {
                System.out.println("file doesn't exist");
            }

        } catch (IOException e) {
            System.out.println("Критична помилка при завантаженні даних: " + e.getMessage());
            System.exit(0);
        }
    }
    //Збереження
    public static void saveData() {
        //Створіть директорію, якщо її немає
        new File("src/data").mkdirs();

        //Збереження автівок
        try {
            mapper.writeValue(new File(Path.CARS.getPath()), cars);
            System.out.println("Вдале збереження автівок!");
        } catch (IOException e) {
            System.out.println("Помилка збереження автівок: " + e.getMessage());
        }
    }

    public static List<Car> getCars() {return cars;}
}
