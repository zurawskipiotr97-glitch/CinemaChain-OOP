package pl.edu.agh.zurawskipiotr.cinemachain.domain.screening;

import pl.edu.agh.zurawskipiotr.cinemachain.domain.booking.Reservation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Per-screening state of seats (availability, reservations, expiration).
 *
 * Hall defines the layout (which seats exist). SeatingPlan defines the dynamic state for a specific screening.
 */
public class SeatingPlan {

    // seatCode -> status
    private final Map<String, SeatStatus> seatStatus = new HashMap<>();

    /**
     * seatCode -> ownerKey
     * ownerKey format:
     *   - C:<customerId>
     *   - G:<token>
     */
    private final Map<String, String> reservedByOwnerKey = new HashMap<>();

    /**
     * ownerKey -> reservation (for preview / printing)
     */
    private final Map<String, Reservation> reservationsByOwnerKey = new HashMap<>();

    /**
     * seatCode -> reservation timestamp (used to expire reservations)
     */
    private final Map<String, LocalDateTime> reservedAtBySeatCode = new HashMap<>();

    private final Duration reservationTtl;

    public SeatingPlan(Collection<String> seatCodes, Duration reservationTtl) {
        Objects.requireNonNull(seatCodes, "seatCodes");
        this.reservationTtl = Objects.requireNonNull(reservationTtl, "reservationTtl");
        for (String code : seatCodes) {
            seatStatus.put(code, SeatStatus.FREE);
        }
    }

    public void reserve(String ownerKey, String... seatCodes) {
        cleanupExpiredReservations(LocalDateTime.now());
        validateSeatCodesProvided(seatCodes);

        for (String code : seatCodes) {
            SeatStatus status = seatStatus.get(code);
            if (status == null) throw new IllegalArgumentException("No such seat in this hall: " + code);
            if (status != SeatStatus.FREE) {
                throw new IllegalStateException("Seat not available: " + code + " (status=" + status + ")");
            }
        }

        LocalDateTime reservedAt = LocalDateTime.now();
        for (String code : seatCodes) {
            seatStatus.put(code, SeatStatus.RESERVED);
            reservedByOwnerKey.put(code, ownerKey);
            reservedAtBySeatCode.put(code, reservedAt);
        }

        Reservation current = reservationsByOwnerKey.get(ownerKey);
        List<String> merged = new ArrayList<>();
        if (current != null) merged.addAll(current.seatCodes());
        merged.addAll(Arrays.asList(seatCodes));
        reservationsByOwnerKey.put(ownerKey, new Reservation(ownerKey, List.copyOf(merged), reservedAt));
    }

    /**
     * Verifies whether seatCodes can be purchased by a given actor (customer or guest token).
     */
    public void authorizePurchase(String customerOwnerKey, String guestOwnerKey, boolean isGuestWithoutToken, String... seatCodes) {
        cleanupExpiredReservations(LocalDateTime.now());
        validateSeatCodesProvided(seatCodes);

        for (String code : seatCodes) {
            SeatStatus status = seatStatus.get(code);
            if (status == null) throw new IllegalArgumentException("No such seat in this hall: " + code);

            if (status == SeatStatus.SOLD) {
                throw new IllegalStateException("Seat already sold: " + code);
            }

            if (status == SeatStatus.RESERVED) {
                if (isGuestWithoutToken) {
                    throw new IllegalStateException("Seat is reserved (guest cannot buy without token): " + code);
                }

                String ownerKey = reservedByOwnerKey.get(code);

                if (customerOwnerKey != null) {
                    if (ownerKey == null || !ownerKey.equals(customerOwnerKey)) {
                        throw new IllegalStateException("Seat reserved by another customer: " + code);
                    }
                }

                if (guestOwnerKey != null) {
                    if (ownerKey == null || !ownerKey.equals(guestOwnerKey)) {
                        throw new IllegalStateException("Seat reserved by someone else (invalid token): " + code);
                    }
                }
            }
        }
    }

    public void markSold(String seatCode) {
        SeatStatus status = seatStatus.get(seatCode);
        if (status == null) throw new IllegalArgumentException("No such seat in this hall: " + seatCode);
        seatStatus.put(seatCode, SeatStatus.SOLD);
        reservedByOwnerKey.remove(seatCode);
        reservedAtBySeatCode.remove(seatCode);
    }

    public SeatStatus getSeatStatus(String seatCode) {
        SeatStatus status = seatStatus.get(seatCode);
        if (status == null) throw new IllegalArgumentException("No such seat: " + seatCode);
        return status;
    }

    public List<String> getReservedSeats(String ownerKey) {
        cleanupExpiredReservations(LocalDateTime.now());
        Reservation r = reservationsByOwnerKey.get(ownerKey);
        return (r == null) ? List.of() : r.seatCodes();
    }

    public void removeSeatCodesFromReservations(String ownerKey, String... seatCodes) {
        if (ownerKey == null) return;

        Reservation r = reservationsByOwnerKey.get(ownerKey);
        if (r == null || r.seatCodes().isEmpty()) return;

        Set<String> codes = new HashSet<>(Arrays.asList(seatCodes));
        List<String> updated = new ArrayList<>(r.seatCodes());
        updated.removeIf(codes::contains);

        if (updated.isEmpty()) {
            reservationsByOwnerKey.remove(ownerKey);
            return;
        }

        reservationsByOwnerKey.put(ownerKey, new Reservation(ownerKey, List.copyOf(updated), r.createdAt()));
    }

    public Map<String, SeatStatus> seatStatusSnapshot() {
        return Collections.unmodifiableMap(seatStatus);
    }

    public Collection<Reservation> reservationsSnapshot() {
        return List.copyOf(reservationsByOwnerKey.values());
    }

    // =========================
    // Expiration

    private void cleanupExpiredReservations(LocalDateTime now) {
        if (reservationTtl.isZero() || reservationTtl.isNegative()) return;

        List<String> expiredSeatCodes = new ArrayList<>();
        for (Map.Entry<String, LocalDateTime> e : reservedAtBySeatCode.entrySet()) {
            String seatCode = e.getKey();
            LocalDateTime reservedAt = e.getValue();
            if (reservedAt == null) continue;

            if (now.isAfter(reservedAt.plus(reservationTtl))
                    && seatStatus.get(seatCode) == SeatStatus.RESERVED) {
                expiredSeatCodes.add(seatCode);
            }
        }

        for (String seatCode : expiredSeatCodes) {
            String ownerKey = reservedByOwnerKey.remove(seatCode);
            reservedAtBySeatCode.remove(seatCode);
            seatStatus.put(seatCode, SeatStatus.FREE);

            if (ownerKey != null) {
                removeSeatCodesFromReservations(ownerKey, seatCode);
            }
        }
    }

    private void validateSeatCodesProvided(String... seatCodes) {
        if (seatCodes == null || seatCodes.length == 0) {
            throw new IllegalArgumentException("No seat codes provided");
        }
    }
}
