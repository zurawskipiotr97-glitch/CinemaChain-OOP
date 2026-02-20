import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cinema {
    private final String id;
    private String name;
    private String address;

    private List<Hall> rooms = new ArrayList<>();
    private List<Movie> movies = new ArrayList<>();
    private List<Screening> screenings = new ArrayList<>();


    public Cinema(String name, String address) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
    }

    public Movie findMovieByTitle(String title) {
        return movies.stream()
                .filter(m -> m.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Hall> getRooms() {
        return rooms;
    }

    public void setRooms(List<Hall> rooms) {
        this.rooms = rooms;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<Screening> screenings) {
        this.screenings = screenings;
    }
}
