package pl.edu.agh.zurawskipiotr.cinemachain.domain.screening;

import pl.edu.agh.zurawskipiotr.cinemachain.domain.customer.Customer;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.catalog.Movie;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.sales.Ticket;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.sales.TicketPurchase;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.venue.Hall;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.venue.Seat;
import pl.edu.agh.zurawskipiotr.cinemachain.pricing.DefaultPricingPolicy;
import pl.edu.agh.zurawskipiotr.cinemachain.pricing.PricingPolicy;

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
    private final SeatingPlan seatingPlan;

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
        this.pricingPolicy = Objects.requireNonNull(pricingPolicy, "pricingPolicy");

        List<String> seatCodes = hall.getSeats().stream().map(Seat::getCode).toList();
        this.seatingPlan = new SeatingPlan(seatCodes, Objects.requireNonNull(reservationTtl, "reservationTtl"));
    }

    public Screening(Movie movie, Hall hall, boolean isVip, boolean isThreeD, LocalDateTime startTime) {
        this(movie, hall, isVip, isThreeD, startTime, Duration.ofMinutes(30), DefaultPricingPolicy.defaultPolicy());
    }

    public void reservePlaces(Customer customer, String... seatCodes) {
        Objects.requireNonNull(customer, "customer");
        seatingPlan.reserve(ownerKeyForCustomer(customer), seatCodes);
    }

    public String reservePlaces(String... seatCodes) {
        String token = generateReservationToken();
        seatingPlan.reserve(ownerKeyForGuest(token), seatCodes);
        return token;
    }

    public List<TicketPurchase> buyTicketsAsGuest(String... seatCodes) {
        return buyTicketsInternal(null, null, seatCodes);
    }

    public List<TicketPurchase> buyTicketsForCustomer(Customer customer, String... seatCodes) {
        Objects.requireNonNull(customer, "customer");
        return buyTicketsInternal(customer, null, seatCodes);
    }

    public List<TicketPurchase> buyTicketsAsGuestWithToken(String reservationToken, String... seatCodes) {
        if (reservationToken == null || reservationToken.isBlank()) {
            throw new IllegalArgumentException("Reservation token is required");
        }
        return buyTicketsInternal(null, reservationToken, seatCodes);
    }

    private List<TicketPurchase> buyTicketsInternal(Customer customerOrNull, String tokenOrNull, String... seatCodes) {
        String customerOwnerKey = (customerOrNull == null) ? null : ownerKeyForCustomer(customerOrNull);
        String guestOwnerKey = (tokenOrNull == null) ? null : ownerKeyForGuest(tokenOrNull);
        boolean isGuestWithoutToken = (customerOrNull == null && tokenOrNull == null);

        seatingPlan.authorizePurchase(customerOwnerKey, guestOwnerKey, isGuestWithoutToken, seatCodes);

        List<TicketPurchase> purchases = new ArrayList<>(seatCodes.length);
        for (String code : seatCodes) {
            Seat seat = findSeatByCode(code);

            BigDecimal price = pricingPolicy.calculatePrice(seat, isVip(), isThreeD());
            Ticket ticket = new Ticket(this, seat, customerOrNull);

            purchases.add(new TicketPurchase(ticket, price));
            seatingPlan.markSold(code);
            soldTicketsByCode.put(ticket.getCode(), ticket);

            if (customerOrNull != null) {
                customerOrNull.addOwnTicket(ticket);
            }
        }

        if (customerOwnerKey != null) seatingPlan.removeSeatCodesFromReservations(customerOwnerKey, seatCodes);
        if (guestOwnerKey != null) seatingPlan.removeSeatCodesFromReservations(guestOwnerKey, seatCodes);

        return purchases;
    }

    public Ticket findTicketByCode(String ticketCode) {
        if (ticketCode == null) return null;
        return soldTicketsByCode.get(ticketCode);
    }

    public SeatStatus getSeatStatus(String seatCode) {
        return seatingPlan.getSeatStatus(seatCode);
    }

    public List<String> getReservedSeatsFor(Customer customer) {
        Objects.requireNonNull(customer, "customer");
        return seatingPlan.getReservedSeats(ownerKeyForCustomer(customer));
    }

    public List<String> getReservedSeatsForToken(String reservationToken) {
        if (reservationToken == null || reservationToken.isBlank()) return List.of();
        return seatingPlan.getReservedSeats(ownerKeyForGuest(reservationToken));
    }

    public void printSummary() {
        System.out.println("Screening: " + movie.title() + " | " + startTime
                + " | hall=" + hall.getName()
                + (isVip ? " | VIP" : "")
                + (isThreeD ? " | 3D" : ""));
    }

    public void printSeatMap() {
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
                SeatStatus status = seatingPlan.getSeatStatus(code);

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

        var reservations = seatingPlan.reservationsSnapshot();
        if (!reservations.isEmpty()) {
            System.out.println("Reserved seats detail:");
            reservations.stream()
                    .sorted(Comparator.comparing(r -> r.ownerKey()))
                    .forEach(r -> System.out.println("- " + r.ownerKey() + " -> " + r.seatCodes() + " | createdAt=" + r.createdAt()));
        }
    }

    public void printReservations() {
        printSummary();
        var reservations = seatingPlan.reservationsSnapshot();
        if (reservations.isEmpty()) {
            System.out.println("Reservations: <none>");
            return;
        }
        System.out.println("Reservations:");
        reservations.stream()
                .sorted(Comparator.comparing(r -> r.ownerKey()))
                .forEach(r -> System.out.println("- " + r.ownerKey() + " -> " + r.seatCodes() + " | createdAt=" + r.createdAt()));
    }

    private Seat findSeatByCode(String code) {
        for (Seat s : hall.getSeats()) {
            if (s.getCode().equals(code)) return s;
        }
        throw new IllegalArgumentException("No such seat in hall: " + code);
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

    public Movie getMovie() {
        return movie;
    }

    public Hall getHall() {
        return hall;
    }

    public boolean isVip() {
        return isVip;
    }

    public boolean isThreeD() {
        return isThreeD;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public PricingPolicy getPricingPolicy() {
        return pricingPolicy;
    }

    public Map<String, SeatStatus> seatStatus() {
        return seatingPlan.seatStatusSnapshot();
    }
}
