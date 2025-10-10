package rental;

public enum Path {
    CLIENTS("src/data/clients.json"),
    CARS("src/data/cars.json"),
    RENTALS("src/data/rentals.json");

    private final String path;

    Path(String path){
        this.path=path;
    }

    public String getPath(){
        return path;
    }

    @Override
    public String toString() {
        return path;
    }
}
