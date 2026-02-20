package pl.edu.agh.zurawskipiotr.cinemachain.model;

import pl.edu.agh.zurawskipiotr.cinemachain.enums.Genre;

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