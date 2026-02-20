package pl.edu.agh.zurawskipiotr.cinemachain.domain.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record Reservation(String ownerKey, List<String> seatCodes, LocalDateTime createdAt) {
    public Reservation {
        Objects.requireNonNull(ownerKey, "ownerKey");
        Objects.requireNonNull(seatCodes, "seatCodes");
        Objects.requireNonNull(createdAt, "createdAt");
    }
}
