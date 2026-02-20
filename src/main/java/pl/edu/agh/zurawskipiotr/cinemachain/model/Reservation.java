package pl.edu.agh.zurawskipiotr.cinemachain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * A lightweight value object representing a reservation owned by a customer or guest token.
 *
 * ownerKey format:
 *  - C:<customerId>
 *  - G:<token>
 */
public record Reservation(String ownerKey, List<String> seatCodes, LocalDateTime createdAt) {
        public Reservation {
                Objects.requireNonNull(ownerKey, "ownerKey");
                Objects.requireNonNull(seatCodes, "seatCodes");
                Objects.requireNonNull(createdAt, "createdAt");
        }
}
