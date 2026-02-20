package pl.edu.agh.zurawskipiotr.cinemachain.domain.venue;

import java.util.Objects;

public record Seat(String row, int number, SeatCategory category) {

    public Seat {
        row = normalizeRow(row);
        Objects.requireNonNull(category, "category");

        if (number <= 0) {
            throw new IllegalArgumentException("pl.edu.agh.zurawskipiotr.cinemachain.model.Seat number must be > 0");
        }
    }

    private static String normalizeRow(String row) {
        if (row == null || row.trim().isEmpty()) throw new IllegalArgumentException("Row is empty");
        return row.trim().toUpperCase();
    }

    public String getCode() {
        return row + number;
    }
}
