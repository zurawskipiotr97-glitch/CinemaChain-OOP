import java.util.ArrayList;
import java.util.List;

public class Cinema {
    String name;
    String address;

    private List<Hall> rooms = new ArrayList<>();
    private List<Movie> movies = new ArrayList<>();
    private List<Screening> screenings = new ArrayList<>();




    public Movie findMovieByTitle(String title) {
        return movies.stream()
                .filter(m -> m.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }
}
