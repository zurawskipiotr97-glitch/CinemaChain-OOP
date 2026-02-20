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
        // REZERWACJE - OVERLOADY
        // =========================================================

        // (A) Klient: rezerwacja po kodach
        public void reservePlaces(Customer customer, String... seatCodes) {
                Objects.requireNonNull(customer, "customer");
                reservePlacesInternal(ownerKeyForCustomer(customer), seatCodes);
        }

        // (B) Klient: rezerwacja po Seat...
        public void reservePlaces(Customer customer, Seat... seats) {
                Objects.requireNonNull(customer, "customer");
                reservePlaces(customer, toSeatCodes(seats));
        }

        // (C) Gość: rezerwacja po kodach -> ZWRACA TOKEN
        public String reservePlaces(String... seatCodes) {
                String token = generateReservationToken();
                reservePlacesInternal(ownerKeyForGuest(token), seatCodes);
                return token;
        }

        // (D) Gość: rezerwacja po Seat... -> ZWRACA TOKEN
        public String reservePlaces(Seat... seats) {
                return reservePlaces(toSeatCodes(seats));
        }

        private void reservePlacesInternal(String ownerKey, String... seatCodes) {
                validateSeatCodesProvided(seatCodes);

                // 1) walidacja: wszystkie miejsca muszą być FREE
                for (String code : seatCodes) {
                        SeatStatus status = seatStatus.get(code);
                        if (status == null) throw new IllegalArgumentException("No such seat in this hall: " + code);
                        if (status != SeatStatus.FREE) {
                                throw new IllegalStateException("Seat not available: " + code + " (status=" + status + ")");
                        }
                }

                // 2) rezerwacja: ustaw RESERVED + zapamiętaj właściciela
                for (String code : seatCodes) {
                        seatStatus.put(code, SeatStatus.RESERVED);
                        reservedByOwnerKey.put(code, ownerKey);
                }

                reservationsByOwnerKey
                        .computeIfAbsent(ownerKey, k -> new ArrayList<>())
                        .addAll(Arrays.asList(seatCodes));
        }

        // =========================================================
        // KUPNO BILETÓW
        // =========================================================

        // gość bez rezerwacji: może kupić tylko FREE (RESERVED zablokowane)
        public List<TicketPurchase> buyTickets(String... seatCodes) {
                return buyTickets((Customer) null, seatCodes);
        }

        // klient: może kupić FREE oraz RESERVED tylko jeśli to jego rezerwacja
        public List<TicketPurchase> buyTickets(Customer customerOrNull, String... seatCodes) {
                validateSeatCodesProvided(seatCodes);

                String customerOwnerKey = (customerOrNull == null) ? null : ownerKeyForCustomer(customerOrNull);

                // 1) WALIDACJA UPRAWNIEŃ DO ZAKUPU
                for (String code : seatCodes) {
                        SeatStatus status = seatStatus.get(code);
                        if (status == null) throw new IllegalArgumentException("No such seat in this hall: " + code);

                        if (status == SeatStatus.SOLD) {
                                throw new IllegalStateException("Seat already sold: " + code);
                        }

                        if (status == SeatStatus.RESERVED) {
                                // gość bez tokena nie może kupić zarezerwowanego miejsca
                                if (customerOrNull == null) {
                                        throw new IllegalStateException("Seat is reserved (guest cannot buy without token): " + code);
                                }
                                String ownerKey = reservedByOwnerKey.get(code);
                                if (ownerKey == null || !ownerKey.equals(customerOwnerKey)) {
                                        throw new IllegalStateException("Seat reserved by another customer: " + code);
                                }
                        }
                        // FREE -> OK
                }

                // 2) REALIZACJA ZAKUPU
                List<TicketPurchase> purchases = new ArrayList<>(seatCodes.length);
                for (String code : seatCodes) {
                        Seat seat = findSeatByCode(code);

                        BigDecimal price = pricingPolicy.calculatePrice(this, seat);
                        Ticket ticket = new Ticket(this, seat, customerOrNull);

                        purchases.add(new TicketPurchase(ticket, price));
                        seatStatus.put(code, SeatStatus.SOLD);

                        reservedByOwnerKey.remove(code);

                        // REJESTR LOKALNY
                        soldTicketsByCode.put(ticket.getCode(), ticket);

                        if (customerOrNull != null) {
                                customerOrNull.addOwnTicket(ticket);
                        }
                }

                // 3) sprzątanie rezerwacji tylko właściciela (klient)
                if (customerOrNull != null) {
                        removeSeatCodesFromReservations(customerOwnerKey, seatCodes);
                }

                return purchases;
        }

        // ✅ Gość kupuje RESERVED używając tokena — FIX: token + MIN 1 miejsce
        public List<TicketPurchase> buyTickets(String reservationToken, String firstSeatCode, String... otherSeatCodes) {
                if (reservationToken == null || reservationToken.isBlank()) {
                        throw new IllegalArgumentException("Reservation token is required");
                }
                if (firstSeatCode == null || firstSeatCode.isBlank()) {
                        throw new IllegalArgumentException("At least one seat code is required");
                }

                String[] seatCodes = mergeFirstAndVarargs(firstSeatCode, otherSeatCodes);
                validateSeatCodesProvided(seatCodes);

                String guestOwnerKey = ownerKeyForGuest(reservationToken);

                // 1) WALIDACJA UPRAWNIEŃ DO ZAKUPU (dla gościa z tokenem)
                for (String code : seatCodes) {
                        SeatStatus status = seatStatus.get(code);
                        if (status == null) throw new IllegalArgumentException("No such seat in this hall: " + code);

                        if (status == SeatStatus.SOLD) {
                                throw new IllegalStateException("Seat already sold: " + code);
                        }

                        if (status == SeatStatus.RESERVED) {
                                String ownerKey = reservedByOwnerKey.get(code);
                                if (ownerKey == null || !ownerKey.equals(guestOwnerKey)) {
                                        throw new IllegalStateException("Seat reserved by someone else (invalid token): " + code);
                                }
                        }
                        // FREE -> OK (gość z tokenem może też kupić FREE)
                }

                // 2) REALIZACJA ZAKUPU (Ticket ma customer=null)
                List<TicketPurchase> purchases = new ArrayList<>(seatCodes.length);
                for (String code : seatCodes) {
                        Seat seat = findSeatByCode(code);

                        BigDecimal price = pricingPolicy.calculatePrice(this, seat);
                        Ticket ticket = new Ticket(this, seat, null);

                        purchases.add(new TicketPurchase(ticket, price));
                        seatStatus.put(code, SeatStatus.SOLD);

                        reservedByOwnerKey.remove(code);

                        // REJESTR LOKALNY
                        soldTicketsByCode.put(ticket.getCode(), ticket);
                }

                // 3) sprzątanie rezerwacji gościa (token)
                removeSeatCodesFromReservations(guestOwnerKey, seatCodes);

                return purchases;
        }

        // overload: token + Seat...
        public List<TicketPurchase> buyTickets(String reservationToken, Seat... seats) {
                String[] codes = toSeatCodes(seats);
                // MIN 1 miejsce gwarantowane przez toSeatCodes
                return buyTickets(reservationToken, codes[0], Arrays.copyOfRange(codes, 1, codes.length));
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

        private String[] toSeatCodes(Seat... seats) {
                if (seats == null || seats.length == 0) {
                        throw new IllegalArgumentException("No seats provided");
                }
                String[] codes = new String[seats.length];
                for (int i = 0; i < seats.length; i++) {
                        if (seats[i] == null) throw new IllegalArgumentException("Seat at index " + i + " is null");
                        codes[i] = seats[i].getCode();
                }
                return codes;
        }

        private String[] mergeFirstAndVarargs(String first, String... rest) {
                int restLen = (rest == null) ? 0 : rest.length;
                String[] out = new String[1 + restLen];
                out[0] = first;
                if (restLen > 0) System.arraycopy(rest, 0, out, 1, restLen);
                return out;
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

        // =========================================================
        // ZAKUP + REJESTRACJA W CinemaChain (bez konfliktu overloadów)
        // =========================================================

        // Gość / bez tokena: rejestracja w CinemaChain
        public List<TicketPurchase> buyTicketsAndRegister(CinemaChain chain, String... seatCodes) {
                Objects.requireNonNull(chain, "chain");
                List<TicketPurchase> purchases = buyTickets(seatCodes);
                for (TicketPurchase tp : purchases) {
                        chain.addTicket(tp.ticket());
                }
                return purchases;
        }

        // Gość z tokenem: rejestracja w CinemaChain
        public List<TicketPurchase> buyTicketsWithReservationAndRegister(
                CinemaChain chain,
                String reservationToken,
                String firstSeatCode,
                String... otherSeatCodes
        ) {
                Objects.requireNonNull(chain, "chain");
                List<TicketPurchase> purchases = buyTickets(reservationToken, firstSeatCode, otherSeatCodes);
                for (TicketPurchase tp : purchases) {
                        chain.addTicket(tp.ticket());
                }
                return purchases;
        }

        // Klient: rejestracja w CinemaChain
        public List<TicketPurchase> buyTicketsForCustomerAndRegister(
                CinemaChain chain,
                Customer customer,
                String... seatCodes
        ) {
                Objects.requireNonNull(chain, "chain");
                Objects.requireNonNull(customer, "customer");
                List<TicketPurchase> purchases = buyTickets(customer, seatCodes);
                for (TicketPurchase tp : purchases) {
                        chain.addTicket(tp.ticket());
                }
                return purchases;
        }
}