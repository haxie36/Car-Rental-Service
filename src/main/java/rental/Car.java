package rental;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Car extends DataItem {
    private String brand;
    private String model;
    private int year;
    private int mileage;
    private double pricePerDay;

    @JsonIgnore
    private transient List<Rental> rentals = new ArrayList<>();

    public Car(){}
    @JsonCreator
    public Car(@JsonProperty("id") String id,
               @JsonProperty("brand") String brand,
               @JsonProperty("model") String model,
               @JsonProperty("year") int year,
               @JsonProperty("mileage") int mileage,
               @JsonProperty("pricePerDay") double pricePerDay) {
        super(id);
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.mileage = mileage;
        this.pricePerDay = pricePerDay;
    }

    //Гетери та сетери
    @Override
    public String getId() {return id;}
    public String getBrand() {return brand;}
    public String getModel() {return model;}
    public int getYear() {return year;}
    public int getMileage() {return mileage;}
    public double getPricePerDay() {return pricePerDay;}
    public void setMileage(int mileage) {this.mileage = mileage;}
    @JsonIgnore
    public List<Rental> getRentals() {return rentals;}
    @JsonIgnore
    public void setRentals(List<Rental> rentals) {this.rentals = rentals;}

    //Чи є автівка вільною на проміжку часу
    public boolean isAvailable(LocalDate startDate, LocalDate endDate) {
        if (rentals == null||rentals.isEmpty()) return true;

        for (Rental rental : rentals) {
            //Перевірка на конфлікт дат
            if (datesOverlap(rental.getStartDate(), rental.getEndDate(), startDate, endDate)) {
                return false;
            }
        }
        return true;
    }

    //Допоміжний метод для перевірки перетину дат
    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    //Додавання та видалення оренди
    public void addRental(Rental rental){rentals.add(rental);}
    public void removeRental(Rental rental){rentals.remove(rental);}

    //Json формат
    @Override
    public String toString() {
        return "Car{" +
                "id='" + id + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", mileage=" + mileage +
                ", pricePerDay=" + pricePerDay +
                '}';
    }
}
