package pl.edu.agh.zurawskipiotr.cinemachain.domain.catalog;



import java.util.List;

public record Movie(
        String title,
        int durationMinutes,
        List<Genre> genres,
        int ageRestriction
) {
    public Movie {
        genres = List.copyOf(genres); // defensywna kopia
    }
}