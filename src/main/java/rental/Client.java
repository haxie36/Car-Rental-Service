package rental;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Client extends DataItem {
    private String name;
    private String phone;
    private List<String> rentalIds;

    @JsonIgnore
    private List<Rental> rentals;

    public Client(){}

    @JsonCreator
    public Client(@JsonProperty("id") String _id,
                  @JsonProperty("name") String _name,
                  @JsonProperty("phone") String _phone,
                  @JsonProperty("rentalIds") ArrayList<String> _rentalIds){
        super(_id);
        name = _name;
        phone = _phone;
        rentalIds = _rentalIds != null ? _rentalIds : new ArrayList<>();
        rentals = new ArrayList<>();
    }

    public Client(String _id, String _name, String _phone){
        super(_id);
        name = _name;
        phone = _phone;
        rentalIds = new ArrayList<>();
        rentals = new ArrayList<>();
    }

    //Гетери та сетери
    @Override
    public String getId(){return id;}
    public String getName(){return name;}
    public String getPhone(){return phone;}
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }

    //Все пов'язане з орендами
    @JsonIgnore
    public List<Rental> getRentals() {return rentals;}
    public List<String> getRentalIds() {return rentalIds;}

    //Перевірка того, чи має клієнт хоча б 1 оренду
    public boolean hasActiveRentals() {
        for (Rental rental : rentals) {
            if (rental.isActive()) {
                return true;
            }
        }
        return false;
    }

    //Додавання оренди
    public void addRental(Rental rental) {
        if (!rentalIds.contains(rental.getId())) {
            rentalIds.add(rental.getId()); //Додавання Id
            rentals.add(rental); //Додавання оренди
        }
    }
    //Видалення оренди
    public void removeRental(Rental rental) {
        rentalIds.remove(rental.getId()); //Id
        rentals.remove(rental); //Оренда
    }

    //Додавання оренд зі списку
    public void updateRentalReferences(List<Rental> allRentals) {
        this.rentals.clear(); //Очищення
        this.rentalIds.clear(); //Очищення
        for (Rental rental : allRentals){
            if (rental.getClientId() != null && rental.getClientId().equals(this.id)) { //Якщо оренда має цього клієнта як її власника
                addRental(rental); //Додавання
            }
        }
    }

    @JsonIgnore //Кількість оренд клієнта
    public int getRentalCount(){return rentalIds.size();}

    //Json формат
    @Override
    public String toString() {
        return "Client{" +
                "type='client'" +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                "rentalIds=" + rentalIds +
                '}';
    }
}
