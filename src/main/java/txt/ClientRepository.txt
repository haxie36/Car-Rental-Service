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

public final class ClientRepository {
//    private static ClientRepository instance; //Singleton pattern
    private static List<Client> clients= new ArrayList<>();
    private final static ObjectMapper mapper = new ObjectMapper() {{
        enable(SerializationFeature.INDENT_OUTPUT); //Додає перенесення рядків та відступи
        registerModule(new JavaTimeModule()); //Для роботи з LocalDate
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }};

    private ClientRepository() {};
//    //Singleton конструктор
//    private ClientRepository(ObjectMapper mapper) {
//        ClientRepository.mapper = mapper;
//    }
//
//    //Singleton метод
//    public static ClientRepository getInstance(ObjectMapper mapper) {
//        if (instance == null) {
//            instance = new ClientRepository(mapper);
//        }
//        return instance;
//    }
    //Завантаження "БД" з Json
    public static void loadData(){
        try {
            File ClientJson = new File(Path.CLIENTS.getPath());
            if (ClientJson.exists()){
                clients = mapper.readValue(ClientJson, new TypeReference<>() {});
                System.out.println("Користувачі успішно завантажені ("+clients.size()+")");
            }else {
                System.out.println("file doesn't exist");
            }
        }catch (IOException e) {
            System.out.println("Критична помилка при завантаженні даних: " + e.getMessage());
            System.exit(0);
        }
    }
    //Збереження
    public static void saveData() {
        //Створіть директорію, якщо її немає
        new File("src/data").mkdirs();

        //Збереження клієнтів
        try {
            mapper.writeValue(new File(Path.CLIENTS.getPath()), clients);
            System.out.println("Вдале збереження користувачів!");
        } catch (IOException e) {
            System.out.println("Помилка збереження користувачів: " + e.getMessage());
        }
    }
    public static List<Client> getClients(){return clients;}
}
