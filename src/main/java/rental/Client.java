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

    public boolean hasActiveRentals() {
        for (Rental rental : rentals) {
            if (rental.isActive()) {
                return true;
            }
        }
        return false;
    }

    public void addRental(Rental rental) {
        if (!rentalIds.contains(rental.getId())) {
            rentalIds.add(rental.getId());
            rentals.add(rental);
        }
    }
    public void removeRental(Rental rental) {
        rentalIds.remove(rental.getId());
        rentals.remove(rental);
    }

    public void updateRentalReferences(List<Rental> allRentals) {
        this.rentals.clear();
        this.rentalIds.clear();
        for (Rental rental : allRentals){
            if (rental.getClientId() != null && rental.getClientId().equals(this.id)) {
                addRental(rental);
            }
        }
    }

    @JsonIgnore
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
