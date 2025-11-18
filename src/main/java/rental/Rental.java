package rental;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Rental extends DataItem {
    private String carId;
    private String clientId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;

    @JsonIgnore
    private transient Car car;
    @JsonIgnore
    private transient Client client;


    public Rental(){}
    @JsonCreator
    public Rental(@JsonProperty("id") String id,
                   @JsonProperty("carId") String carId,
                   @JsonProperty("clientId") String clientId,
                   @JsonProperty("startDate") LocalDate startDate,
                   @JsonProperty("endDate") LocalDate endDate,
                  @JsonProperty("totalPrice") double totalPrice) {
        super(id);
        this.carId = carId;
        this.clientId = clientId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
    }

    @JsonIgnore
    public Rental(String id, String carId, String clientId, LocalDate startDate, LocalDate endDate) {
        super(id);
        this.carId = carId;
        this.clientId = clientId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    //Кількість днів від початку до кінця
    public long totalDays(){return ChronoUnit.DAYS.between(startDate,endDate);}

    @JsonIgnore //Чи є оренда активною сьогодні
    public boolean isActive(){
        LocalDate today = LocalDate.now();
        return (today.isEqual(startDate) || today.isAfter(startDate)) &&
                (today.isEqual(endDate) || today.isBefore(endDate));
    }

    //Гетери та сетери
    @Override
    public String getId() {return id;}
    public String getCarId() {return carId;}
    public String getClientId() {return clientId;}
    public LocalDate getStartDate() {return startDate;}
    public LocalDate getEndDate() {return endDate;}
    public double getTotalPrice(){return totalPrice;}
    public void setCarId(String carId) {this.carId = carId;}
    public void setClientId(String clientId) {this.clientId = clientId;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}
    public void setTotalPrice(double totalPrice) {this.totalPrice = totalPrice;}

    //Гетери для об'єктів
    @JsonIgnore
    public Car getCar() { return car; }
    @JsonIgnore
    public Client getClient() { return client; }

    //Сетери для оновлення посилань
    @JsonIgnore
    public void setCar(Car car) {
        this.car = car;
        this.carId = car != null ? car.getId() : null;
    }
    @JsonIgnore
    public void setClient(Client client) {
        this.client = client;
        this.clientId = client != null ? client.getId() : null;
    }

    //Json формат
    @Override
    public String toString() {
        return "Rental{" +
                "id='" + id + '\'' +
                ", carId='" + carId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
