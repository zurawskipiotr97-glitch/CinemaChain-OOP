import java.time.LocalDateTime;
import java.util.*;

public class Screening {
        private final Movie movie;
        private final Hall hall;
        private final boolean isVip;
        private final boolean isThreeD;
        private final LocalDateTime startTime;
        private final Map<String, SeatStatus> seatStatus = new HashMap<>();
        private final Map<String, List<String>> reservationsByCustomerId = new HashMap<>();

    public Screening(Movie movie, Hall hall, boolean isVip, boolean isThreeD, LocalDateTime startTime) {
        this.movie = movie;
        this.hall = hall;
        this.isVip = isVip;
        this.isThreeD = isThreeD;
        this.startTime = startTime;

            for (Seat seat : hall.getSeats()) {
                    seatStatus.put(seat.getCode(), SeatStatus.FREE);
            }
    }

        // Rezerwacja BEZ konta
        public void reservePlaces(String... seatCodes) {
                reservePlacesInternal(null, seatCodes);
        }

        // Rezerwacja DLA klienta
        public void reservePlaces(Customer customer, String... seatCodes) {
                Objects.requireNonNull(customer, "customer");
                reservePlacesInternal(customer.getId(), seatCodes);
        }

        private void reservePlacesInternal(String customerIdOrNull, String... seatCodes) {
                if (seatCodes == null || seatCodes.length == 0) {
                        throw new IllegalArgumentException("No seat codes provided");
                }

                // 1) walidacja
                for (String code : seatCodes) {
                        SeatStatus status = seatStatus.get(code);
                        if (status == null) {
                                throw new IllegalArgumentException("No such seat in this hall: " + code);
                        }
                        if (status != SeatStatus.FREE) {
                                throw new IllegalStateException("Seat not available: " + code + " (status=" + status + ")");
                        }
                }

                // 2) rezerwacja
                for (String code : seatCodes) {
                        seatStatus.put(code, SeatStatus.RESERVED);
                }

                // 3) zapis "kto rezerwował" (opcjonalnie)
                reservationsByCustomerId
                        .computeIfAbsent(customerIdOrNull, k -> new ArrayList<>())
                        .addAll(Arrays.asList(seatCodes));
        }

        public SeatStatus getSeatStatus(String seatCode) {
                SeatStatus status = seatStatus.get(seatCode);
                if (status == null) throw new IllegalArgumentException("No such seat: " + seatCode);
                return status;
        }

        public Movie getMovie() { return movie; }
        public Hall getHall() { return hall; }

        public Movie movie() {
                return movie;
        }

        public Hall hall() {
                return hall;
        }

        public boolean isVip() {
                return isVip;
        }

        public LocalDateTime startTime() {
                return startTime;
        }

        public Map<String, SeatStatus> seatStatus() {
                return Collections.unmodifiableMap(seatStatus);
        }

        public Map<String, List<String>> reservationsByCustomerId() {
                return Collections.unmodifiableMap(reservationsByCustomerId);
        }

        public boolean isThreeD() { return isThreeD; }
        public LocalDateTime getStartTime() { return startTime; }

}
