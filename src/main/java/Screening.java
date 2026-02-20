import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Screening {
        private final Movie movie;
        private final Hall hall;
        private final boolean isVip;
        private final boolean isThreeD;
        private final LocalDateTime startTime;

        private final PricingPolicy pricingPolicy;

        // seatCode -> status
        private final Map<String, SeatStatus> seatStatus = new HashMap<>();

        /**
         * seatCode -> ownerKey
         * ownerKey:
         *   - "C:<customerId>" dla klienta
         *   - "G:<token>" dla rezerwacji gościa
         */
        private final Map<String, String> reservedByOwnerKey = new HashMap<>();

        /**
         * ownerKey -> list of seatCodes (podgląd rezerwacji)
         */
        private final Map<String, List<String>> reservationsByOwnerKey = new HashMap<>();

        /**
         * Lokalny rejestr sprzedanych biletów na ten seans
         * ticketCode -> Ticket
         */
        private final Map<String, Ticket> soldTicketsByCode = new HashMap<>();

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

        // =========================================================
        // REZERWACJE
        // =========================================================

        // Klient: rezerwacja po kodach
        public void reservePlaces(Customer customer, String... seatCodes) {
                Objects.requireNonNull(customer, "customer");
                reservePlacesInternal(ownerKeyForCustomer(customer), seatCodes);
        }

        // Gość: rezerwacja po kodach -> ZWRACA TOKEN
        public String reservePlaces(String... seatCodes) {
                String token = generateReservationToken();
                reservePlacesInternal(ownerKeyForGuest(token), seatCodes);
                return token;
        }

        private void reservePlacesInternal(String ownerKey, String... seatCodes) {
                validateSeatCodesProvided(seatCodes);

                // wszystkie miejsca muszą być FREE
                for (String code : seatCodes) {
                        SeatStatus status = seatStatus.get(code);
                        if (status == null) throw new IllegalArgumentException("No such seat in this hall: " + code);
                        if (status != SeatStatus.FREE) {
                                throw new IllegalStateException("Seat not available: " + code + " (status=" + status + ")");
                        }
                }

                // rezerwacja
                for (String code : seatCodes) {
                        seatStatus.put(code, SeatStatus.RESERVED);
                        reservedByOwnerKey.put(code, ownerKey);
                }

                reservationsByOwnerKey
                        .computeIfAbsent(ownerKey, k -> new ArrayList<>())
                        .addAll(Arrays.asList(seatCodes));
        }

        // =========================================================
        // ZAKUPY (JAWNE NAZWY -> ZERO AMBIGUITY)
        // =========================================================

        // Gość bez tokena: tylko FREE
        public List<TicketPurchase> buyTicketsAsGuest(String... seatCodes) {
                return buyTicketsInternal(null, null, seatCodes);
        }

        // Klient: FREE + RESERVED tylko swoje
        public List<TicketPurchase> buyTicketsForCustomer(Customer customer, String... seatCodes) {
                Objects.requireNonNull(customer, "customer");
                return buyTicketsInternal(customer, null, seatCodes);
        }

        // Gość z tokenem: FREE + RESERVED tylko tokenowe
        public List<TicketPurchase> buyTicketsAsGuestWithToken(String reservationToken, String... seatCodes) {
                if (reservationToken == null || reservationToken.isBlank()) {
                        throw new IllegalArgumentException("Reservation token is required");
                }
                return buyTicketsInternal(null, reservationToken, seatCodes);
        }

        /**
         * Wspólna logika zakupu:
         * - customerOrNull != null  => klient
         * - tokenOrNull != null     => gość z tokenem
         * - oba null                => gość bez tokena
         */
        private List<TicketPurchase> buyTicketsInternal(Customer customerOrNull, String tokenOrNull, String... seatCodes) {
                validateSeatCodesProvided(seatCodes);

                String customerOwnerKey = (customerOrNull == null) ? null : ownerKeyForCustomer(customerOrNull);
                String guestOwnerKey = (tokenOrNull == null) ? null : ownerKeyForGuest(tokenOrNull);
                boolean isGuestWithoutToken = (customerOrNull == null && tokenOrNull == null);

                // 1) WALIDACJA UPRAWNIEŃ
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

                // 2) REALIZACJA ZAKUPU
                List<TicketPurchase> purchases = new ArrayList<>(seatCodes.length);
                for (String code : seatCodes) {
                        Seat seat = findSeatByCode(code);

                        BigDecimal price = pricingPolicy.calculatePrice(this, seat);
                        Ticket ticket = new Ticket(this, seat, customerOrNull); // owner=null dla gościa

                        purchases.add(new TicketPurchase(ticket, price));
                        seatStatus.put(code, SeatStatus.SOLD);

                        reservedByOwnerKey.remove(code);
                        soldTicketsByCode.put(ticket.getCode(), ticket);

                        if (customerOrNull != null) {
                                customerOrNull.addOwnTicket(ticket);
                        }
                }

                // 3) SPRZĄTANIE REZERWACJI
                if (customerOwnerKey != null) {
                        removeSeatCodesFromReservations(customerOwnerKey, seatCodes);
                }
                if (guestOwnerKey != null) {
                        removeSeatCodesFromReservations(guestOwnerKey, seatCodes);
                }

                return purchases;
        }

        // =========================================================
        // WERYFIKACJA BILETU / PODGLĄD
        // =========================================================

        public Ticket findTicketByCode(String ticketCode) {
                if (ticketCode == null) return null;
                return soldTicketsByCode.get(ticketCode);
        }

        public SeatStatus getSeatStatus(String seatCode) {
                SeatStatus status = seatStatus.get(seatCode);
                if (status == null) throw new IllegalArgumentException("No such seat: " + seatCode);
                return status;
        }

        public List<String> getReservedSeatsFor(Customer customer) {
                Objects.requireNonNull(customer, "customer");
                return reservationsByOwnerKey.getOrDefault(ownerKeyForCustomer(customer), List.of());
        }

        public List<String> getReservedSeatsForToken(String reservationToken) {
                if (reservationToken == null || reservationToken.isBlank()) return List.of();
                return reservationsByOwnerKey.getOrDefault(ownerKeyForGuest(reservationToken), List.of());
        }

        // =========================================================
        // HELPERS
        // =========================================================

        private void removeSeatCodesFromReservations(String ownerKey, String... seatCodes) {
                if (ownerKey == null) return;

                List<String> list = reservationsByOwnerKey.get(ownerKey);
                if (list == null || list.isEmpty()) return;

                Set<String> codes = new HashSet<>(Arrays.asList(seatCodes));
                list.removeIf(codes::contains);

                if (list.isEmpty()) {
                        reservationsByOwnerKey.remove(ownerKey);
                }
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

        private String ownerKeyForCustomer(Customer customer) {
                return "C:" + customer.getId();
        }

        private String ownerKeyForGuest(String token) {
                return "G:" + token;
        }

        private String generateReservationToken() {
                return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        }

        // =========================================================
        // GETTERY
        // =========================================================

        public Movie getMovie() { return movie; }
        public Hall getHall() { return hall; }
        public boolean isVip() { return isVip; }
        public boolean isThreeD() { return isThreeD; }
        public LocalDateTime getStartTime() { return startTime; }
        public PricingPolicy getPricingPolicy() { return pricingPolicy; }

        public Map<String, SeatStatus> seatStatus() { return Collections.unmodifiableMap(seatStatus); }
}