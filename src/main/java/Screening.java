import java.math.BigDecimal;
import java.time.Duration;
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

    /**
     * Reservation time-to-live. After TTL passes, RESERVED seats are released back to FREE.
     */
    private final Duration reservationTtl;


    /**
     * Local registry of tickets sold for this screening.
     * ticketCode -> Ticket
     */
    private final Map<String, Ticket> soldTicketsByCode = new HashMap<>();

    public Screening(
            Movie movie,
            Hall hall,
            boolean isVip,
            boolean isThreeD,
            LocalDateTime startTime,
            Duration reservationTtl,
            PricingPolicy pricingPolicy
    ) {
        this.movie = Objects.requireNonNull(movie, "movie");
        this.hall = Objects.requireNonNull(hall, "hall");
        this.isVip = isVip;
        this.isThreeD = isThreeD;
        this.startTime = Objects.requireNonNull(startTime, "startTime");
        this.reservationTtl = Objects.requireNonNull(reservationTtl, "reservationTtl");
        this.pricingPolicy = Objects.requireNonNull(pricingPolicy, "pricingPolicy");

        for (Seat seat : hall.getSeats()) {
            seatStatus.put(seat.getCode(), SeatStatus.FREE);
        }
    }

    // Convenience constructor with default pricing policy and TTL.
    public Screening(Movie movie, Hall hall, boolean isVip, boolean isThreeD, LocalDateTime startTime) {
        this(movie, hall, isVip, isThreeD, startTime, Duration.ofMinutes(30), DefaultPricingPolicy.defaultPolicy());
    }

    // =========================================================
    // Reservations
    // =========================================================

    // Customer reservation
    public void reservePlaces(Customer customer, String... seatCodes) {
        Objects.requireNonNull(customer, "customer");
        reservePlacesInternal(ownerKeyForCustomer(customer), seatCodes);
    }

    // Guest reservation -> returns token
    public String reservePlaces(String... seatCodes) {
        String token = generateReservationToken();
        reservePlacesInternal(ownerKeyForGuest(token), seatCodes);
        return token;
    }

    private void reservePlacesInternal(String ownerKey, String... seatCodes) {
        cleanupExpiredReservations(LocalDateTime.now());
        validateSeatCodesProvided(seatCodes);

        // all seats must be FREE
        for (String code : seatCodes) {
            SeatStatus status = seatStatus.get(code);
            if (status == null) throw new IllegalArgumentException("No such seat in this hall: " + code);
            if (status != SeatStatus.FREE) {
                throw new IllegalStateException("Seat not available: " + code + " (status=" + status + ")");
            }
        }

        // reserve
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

    // =========================================================
    // Purchases
    // =========================================================

    // Guest without token: FREE only
    public List<TicketPurchase> buyTicketsAsGuest(String... seatCodes) {
        return buyTicketsInternal(null, null, seatCodes);
    }

    // Customer: FREE + own RESERVED
    public List<TicketPurchase> buyTicketsForCustomer(Customer customer, String... seatCodes) {
        Objects.requireNonNull(customer, "customer");
        return buyTicketsInternal(customer, null, seatCodes);
    }

    // Guest with token: FREE + token-owned RESERVED
    public List<TicketPurchase> buyTicketsAsGuestWithToken(String reservationToken, String... seatCodes) {
        if (reservationToken == null || reservationToken.isBlank()) {
            throw new IllegalArgumentException("Reservation token is required");
        }
        return buyTicketsInternal(null, reservationToken, seatCodes);
    }

    /**
     * Shared purchase logic:
     * - customerOrNull != null  => customer
     * - tokenOrNull != null     => guest with token
     * - both null               => guest without token
     */
    private List<TicketPurchase> buyTicketsInternal(Customer customerOrNull, String tokenOrNull, String... seatCodes) {
        cleanupExpiredReservations(LocalDateTime.now());
        validateSeatCodesProvided(seatCodes);

        String customerOwnerKey = (customerOrNull == null) ? null : ownerKeyForCustomer(customerOrNull);
        String guestOwnerKey = (tokenOrNull == null) ? null : ownerKeyForGuest(tokenOrNull);
        boolean isGuestWithoutToken = (customerOrNull == null && tokenOrNull == null);

        // 1) Authorization
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

        // 2) Purchase execution
        List<TicketPurchase> purchases = new ArrayList<>(seatCodes.length);
        for (String code : seatCodes) {
            Seat seat = findSeatByCode(code);

            BigDecimal price = pricingPolicy.calculatePrice(this, seat);
            Ticket ticket = new Ticket(this, seat, customerOrNull); // owner=null dla gościa

            purchases.add(new TicketPurchase(ticket, price));
            seatStatus.put(code, SeatStatus.SOLD);

            reservedByOwnerKey.remove(code);
            reservedAtBySeatCode.remove(code);
            soldTicketsByCode.put(ticket.getCode(), ticket);

            if (customerOrNull != null) {
                customerOrNull.addOwnTicket(ticket);
            }
        }

        // 3) Reservation cleanup
        if (customerOwnerKey != null) {
            removeSeatCodesFromReservations(customerOwnerKey, seatCodes);
        }
        if (guestOwnerKey != null) {
            removeSeatCodesFromReservations(guestOwnerKey, seatCodes);
        }

        return purchases;
    }

    // =========================================================
    // Ticket verification / preview
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
        cleanupExpiredReservations(LocalDateTime.now());
        Reservation r = reservationsByOwnerKey.get(ownerKeyForCustomer(customer));
        return (r == null) ? List.of() : r.seatCodes();
    }

    public List<String> getReservedSeatsForToken(String reservationToken) {
        if (reservationToken == null || reservationToken.isBlank()) return List.of();
        cleanupExpiredReservations(LocalDateTime.now());
        Reservation r = reservationsByOwnerKey.get(ownerKeyForGuest(reservationToken));
        return (r == null) ? List.of() : r.seatCodes();
    }

    // =========================================================
    // PRINTING (demo/debug helpers)

    public void printSummary() {
        System.out.println("Screening: " + movie.title() + " | " + startTime
                + " | hall=" + hall.getName()
                + (isVip ? " | VIP" : "")
                + (isThreeD ? " | 3D" : ""));
    }

    /**
     * Prints seat status for the whole hall.
     * Format: <seatCode> -> <status> [(ownerKey)]
     */
    /**
     * Prints a compact, row-based seat map.
     *
     * Legend:
     *   . = FREE
     *   R = RESERVED
     *   X = SOLD
     */
    public void printSeatMap() {
        cleanupExpiredReservations(LocalDateTime.now());
        printSummary();

        Map<String, List<Seat>> seatsByRow = new TreeMap<>();
        int maxNumber = 0;
        for (Seat seat : hall.getSeats()) {
            seatsByRow.computeIfAbsent(seat.row(), r -> new ArrayList<>()).add(seat);
            maxNumber = Math.max(maxNumber, seat.number());
        }

        int freeCount = 0;
        int reservedCount = 0;
        int soldCount = 0;

        StringBuilder header = new StringBuilder();
        header.append("     ");
        for (int n = 1; n <= maxNumber; n++) {
            header.append(String.format("%3d", n));
        }
        System.out.println(header);
        System.out.println("     " + "-".repeat(Math.max(0, maxNumber * 3)));

        for (Map.Entry<String, List<Seat>> entry : seatsByRow.entrySet()) {
            List<Seat> rowSeats = new ArrayList<>(entry.getValue());
            rowSeats.sort(Comparator.comparingInt(Seat::number));

            Map<Integer, Seat> byNumber = new HashMap<>();
            for (Seat s : rowSeats) {
                byNumber.put(s.number(), s);
            }

            StringBuilder line = new StringBuilder();
            line.append(String.format("%3s |", entry.getKey()));

            for (int n = 1; n <= maxNumber; n++) {
                Seat seat = byNumber.get(n);
                if (seat == null) {
                    line.append("   ");
                    continue;
                }

                String code = seat.getCode();
                SeatStatus status = seatStatus.get(code);

                char mark;
                if (status == SeatStatus.SOLD) {
                    mark = 'X';
                    soldCount++;
                } else if (status == SeatStatus.RESERVED) {
                    mark = 'R';
                    reservedCount++;
                } else {
                    mark = '.';
                    freeCount++;
                }
                line.append(String.format("%3s", mark));
            }
            System.out.println(line);
        }

        System.out.println("Legend: . = FREE | R = RESERVED | X = SOLD");
        System.out.println("Totals: FREE=" + freeCount + " | RESERVED=" + reservedCount + " | SOLD=" + soldCount);

        if (!reservationsByOwnerKey.isEmpty()) {
            System.out.println("Reserved seats detail:");
            reservationsByOwnerKey.values().stream()
                    .sorted(Comparator.comparing(Reservation::ownerKey))
                    .forEach(r -> System.out.println("- " + r.ownerKey() + " -> " + r.seatCodes() + " | createdAt=" + r.createdAt()));
        }
    }




    /**
     * Prints active reservations grouped by ownerKey.
     */
    public void printReservations() {
        cleanupExpiredReservations(LocalDateTime.now());
        printSummary();
        if (reservationsByOwnerKey.isEmpty()) {
            System.out.println("Reservations: <none>");
            return;
        }
        System.out.println("Reservations:");
        reservationsByOwnerKey.values().stream()
                .sorted(Comparator.comparing(Reservation::ownerKey))
                .forEach(r -> System.out.println("- " + r.ownerKey() + " -> " + r.seatCodes() + " | createdAt=" + r.createdAt()));
    }

    // =========================================================
    // HELPERS

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


    // =========================================================

    private void removeSeatCodesFromReservations(String ownerKey, String... seatCodes) {
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