import java.math.BigDecimal;
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

        // DODANE
        private final PricingPolicy pricingPolicy;

        public Screening(
                Movie movie,
                Hall hall,
                boolean isVip,
                boolean isThreeD,
                LocalDateTime startTime,
                PricingPolicy pricingPolicy
        ) {
                this.movie = Objects.requireNonNull(movie, "movie");
                this.hall = Objects.requireNonNull(hall, "hall");
                this.isVip = isVip;
                this.isThreeD = isThreeD;
                this.startTime = Objects.requireNonNull(startTime, "startTime");
                this.pricingPolicy = Objects.requireNonNull(pricingPolicy, "pricingPolicy");

                for (Seat seat : hall.getSeats()) {
                        seatStatus.put(seat.getCode(), SeatStatus.FREE);
                }
        }

        // wygodny konstruktor z domyślną polityką cen
        public Screening(Movie movie, Hall hall, boolean isVip, boolean isThreeD, LocalDateTime startTime) {
                this(movie, hall, isVip, isThreeD, startTime, DefaultPricingPolicy.defaultPolicy());
        }

        // --------------------
        // REZERWACJE (tylko Customer)
        // --------------------

        public void reservePlaces(Customer customer, String... seatCodes) {
                Objects.requireNonNull(customer, "customer");
                reservePlacesInternal(customer.getId(), seatCodes);
        }

        private void reservePlacesInternal(String customerId, String... seatCodes) {
                validateSeatCodesProvided(seatCodes);

                for (String code : seatCodes) {
                        SeatStatus status = seatStatus.get(code);
                        if (status == null) throw new IllegalArgumentException("No such seat in this hall: " + code);
                        if (status != SeatStatus.FREE) throw new IllegalStateException("Seat not available: " + code + " (status=" + status + ")");
                }

                for (String code : seatCodes) {
                        seatStatus.put(code, SeatStatus.RESERVED);
                }

                reservationsByCustomerId
                        .computeIfAbsent(customerId, k -> new ArrayList<>())
                        .addAll(Arrays.asList(seatCodes));
        }

        // --------------------
        // KUPNO BILETÓW (customer może być null)
        // --------------------

        public List<TicketPurchase> buyTickets(CinemaChain chain, String... seatCodes) {
                return buyTickets(chain, null, seatCodes);
        }

        public List<TicketPurchase> buyTickets(CinemaChain chain, Customer customerOrNull, String... seatCodes) {
                Objects.requireNonNull(chain, "chain");
                validateSeatCodesProvided(seatCodes);

                for (String code : seatCodes) {
                        SeatStatus status = seatStatus.get(code);
                        if (status == null) throw new IllegalArgumentException("No such seat in this hall: " + code);
                        if (status == SeatStatus.SOLD) throw new IllegalStateException("Seat already sold: " + code);
                }

                List<TicketPurchase> purchases = new ArrayList<>(seatCodes.length);

                for (String code : seatCodes) {
                        Seat seat = findSeatByCode(code);

                        BigDecimal price = pricingPolicy.calculatePrice(this, seat);

                        Ticket ticket = new Ticket(this, seat, customerOrNull);
                        purchases.add(new TicketPurchase(ticket, price));

                        seatStatus.put(code, SeatStatus.SOLD);

                        chain.addTicket(ticket);
                        if (customerOrNull != null) {
                                customerOrNull.addOwnTicket(ticket);
                        }
                }

                // jeśli klient kupił miejsca, usuń je z jego rezerwacji (żeby nie wisiały)
                removeSeatCodesFromReservations(seatCodes);

                return purchases;
        }

        private void removeSeatCodesFromReservations(String... seatCodes) {
                Set<String> codes = new HashSet<>(Arrays.asList(seatCodes));

                for (Map.Entry<String, List<String>> entry : reservationsByCustomerId.entrySet()) {
                        entry.getValue().removeIf(codes::contains);
                }
                reservationsByCustomerId.entrySet().removeIf(e -> e.getValue().isEmpty());
        }

        private Seat findSeatByCode(String code) {
                for (Seat s : hall.getSeats()) {
                        if (s.getCode().equals(code)) return s;
                }
                throw new IllegalArgumentException("No such seat in hall: " + code);
        }

        private void validateSeatCodesProvided(String... seatCodes) {
                if (seatCodes == null || seatCodes.length == 0) {
                        throw new IllegalArgumentException("No seat codes provided");
                }
        }

        // --------------------
        // PODGLĄD / GETTERY
        // --------------------

        public SeatStatus getSeatStatus(String seatCode) {
                SeatStatus status = seatStatus.get(seatCode);
                if (status == null) throw new IllegalArgumentException("No such seat: " + seatCode);
                return status;
        }

        public List<String> getReservedSeatsFor(Customer customer) {
                Objects.requireNonNull(customer, "customer");
                return reservationsByCustomerId.getOrDefault(customer.getId(), List.of());
        }

        public Movie getMovie() { return movie; }
        public Hall getHall() { return hall; }
        public boolean isVip() { return isVip; }
        public boolean isThreeD() { return isThreeD; }
        public LocalDateTime getStartTime() { return startTime; }

        public Map<String, SeatStatus> seatStatus() { return Collections.unmodifiableMap(seatStatus); }
        public Map<String, List<String>> reservationsByCustomerId() { return Collections.unmodifiableMap(reservationsByCustomerId); }

        public PricingPolicy getPricingPolicy() { return pricingPolicy; }
}