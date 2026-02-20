package pl.edu.agh.zurawskipiotr.cinemachain;

import pl.edu.agh.zurawskipiotr.cinemachain.domain.catalog.Genre;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.venue.SeatCategory;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.catalog.Movie;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.chain.CinemaChain;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.customer.Customer;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.screening.Screening;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.sales.Ticket;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.sales.TicketPurchase;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.venue.Cinema;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.venue.Hall;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.venue.Seat;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CinemaChain chain = new CinemaChain("Multikino");

        // ===== Cinemas =====
        Cinema tarasy = new Cinema("Super Tarasy", "ul. Akademicka 5");
        Cinema vivo = new Cinema("Vivo", "ul. Chopina 42");
        Cinema starowka = new Cinema("Starówka", "ul. Rynek 25/26");

        chain.addCinema(tarasy);
        chain.addCinema(vivo);
        chain.addCinema(starowka);

        // ===== Movies =====
        Movie avatar = new Movie("Avatar: Istota Wody", 162, List.of(Genre.SCI_FI), 12);
        Movie dune = new Movie("Diuna: Część II", 166, List.of(Genre.SCI_FI), 12);
        Movie wish = new Movie("Życzenie", 95, List.of(Genre.ANIMATION, Genre.FAMILY), 0);

        chain.addMovie(avatar);
        chain.addMovie(dune);
        chain.addMovie(wish);

        // ===== Customers =====
        Customer c1 = new Customer("Jan", "Kowalski", "jan.kowalski@mail.com");
        Customer c2 = new Customer("Anna", "Nowak", "anna.nowak@mail.com");
        chain.registerCustomer(c1);
        chain.registerCustomer(c2);

        // ===== Halls + seats =====
        Hall hallTarasy1 = new Hall("Sala 1");
        for (int i = 1; i <= 10; i++) hallTarasy1.addSeat(new Seat("A", i, SeatCategory.STANDARD));
        for (int i = 1; i <= 5; i++) hallTarasy1.addSeat(new Seat("B", i, SeatCategory.VIP));
        tarasy.addHall(hallTarasy1);

        Hall hallVivo1 = new Hall("Sala 1");
        for (int i = 1; i <= 8; i++) hallVivo1.addSeat(new Seat("A", i, SeatCategory.STANDARD));
        for (int i = 1; i <= 4; i++) hallVivo1.addSeat(new Seat("B", i, SeatCategory.VIP));
        vivo.addHall(hallVivo1);

        Hall hallStarowka1 = new Hall("Sala 1");
        for (int i = 1; i <= 6; i++) hallStarowka1.addSeat(new Seat("A", i, SeatCategory.STANDARD));
        starowka.addHall(hallStarowka1);

        // ===== Screenings (within next week) =====
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        Screening sTarasy = new Screening(
                avatar,
                hallTarasy1,
                false,  // VIP screening flag
                true,   // 3D
                now.plusHours(2)
        );
        tarasy.addScreening(sTarasy);

        Screening sVivo = new Screening(
                dune,
                hallVivo1,
                true,   // VIP screening flag
                true,   // 3D
                now.plusDays(1).withHour(19).withMinute(0)
        );
        vivo.addScreening(sVivo);

        Screening sStarowka = new Screening(
                wish,
                hallStarowka1,
                false,
                false,
                now.plusDays(5).withHour(11).withMinute(30)
        );
        starowka.addScreening(sStarowka);

        // pl.edu.agh.zurawskipiotr.cinemachain.model.Screening outside the "next week" window (should not appear in programme for next week)
        Screening outsideWeek = new Screening(
                avatar,
                hallTarasy1,
                false,
                false,
                now.plusDays(10).withHour(20).withMinute(0)
        );
        tarasy.addScreening(outsideWeek);

        // ===== Programme (next week) =====
        chain.printProgramme();

        // ===== pl.edu.agh.zurawskipiotr.cinemachain.model.Seat map (pretty) + reservations + purchases =====
        System.out.println();
        System.out.println("=== pl.edu.agh.zurawskipiotr.cinemachain.model.Seat map: initial state ===");
        sTarasy.printSeatMap();

        System.out.println();
        System.out.println("=== pl.edu.agh.zurawskipiotr.cinemachain.model.Customer reservation (should create RESERVED state) ===");
        sTarasy.reservePlaces(c1, "A6", "A7");
        sTarasy.printReservations();
        sTarasy.printSeatMap();

        System.out.println();
        System.out.println("=== Guest reservation token (should create RESERVED state) ===");
        String token = sTarasy.reservePlaces("A4", "A5");
        sTarasy.printReservations();
        sTarasy.printSeatMap();

        System.out.println();
        System.out.println("=== Guest purchase without token for RESERVED seats (should fail) ===");
        try {
            chain.buyTicketsAsGuest(sTarasy, "A4");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            System.out.println("Purchase rejected: " + ex.getMessage());
        }

        // State should be unchanged after a rejected purchase attempt.
        sTarasy.printSeatMap();

        System.out.println();
        System.out.println("=== pl.edu.agh.zurawskipiotr.cinemachain.model.Customer purchase (FREE seats) ===");
        List<TicketPurchase> p1 = chain.buyTickets(sTarasy, c2, "A1", "A2");
        for (TicketPurchase tp : p1) {
            System.out.println("Bought: " + tp.ticket().getCode() + " | seat=" + tp.ticket().getSeat().getCode() + " | price=" + tp.price());
        }
        sTarasy.printSeatMap();

        System.out.println();
        System.out.println("=== pl.edu.agh.zurawskipiotr.cinemachain.model.Customer purchase of own RESERVED seats ===");
        List<TicketPurchase> p2 = chain.buyTickets(sTarasy, c1, "A6", "A7");
        for (TicketPurchase tp : p2) {
            System.out.println("Bought: " + tp.ticket().getCode() + " | seat=" + tp.ticket().getSeat().getCode() + " | price=" + tp.price());
        }
        sTarasy.printSeatMap();

        System.out.println();
        System.out.println("=== Guest purchase with token for RESERVED seats ===");
        List<TicketPurchase> p3 = chain.buyTicketsWithToken(sTarasy, token, "A4", "A5");
        for (TicketPurchase tp : p3) {
            System.out.println("Bought: " + tp.ticket().getCode() + " | seat=" + tp.ticket().getSeat().getCode() + " | price=" + tp.price());
        }
        sTarasy.printSeatMap();

        System.out.println();
        System.out.println("=== Guest purchase without account (FREE seat) + lookup by code ===");
        List<TicketPurchase> p4 = chain.buyTicketsAsGuest(sTarasy, "A3");
        String guestCode = p4.get(0).ticket().getCode();
        System.out.println("Guest ticket code: " + guestCode);

        Ticket found = chain.findTicketByCode(guestCode);
        System.out.println("Lookup result: " + found.getCode()
                + " | " + found.getScreening().getMovie().title()
                + " | " + found.getScreening().getStartTime()
                + " | seat=" + found.getSeat().getCode());

        System.out.println();
        System.out.println("=== pl.edu.agh.zurawskipiotr.cinemachain.model.Customer tickets ===");
        c1.printOwnTickets();
        c2.printOwnTickets();

        System.out.println();
        chain.printTicketRegistrySummary();

        // ===== Shows that VIP/3D pricing is also present on another screening =====
        System.out.println();
        System.out.println("=== Pricing example for VIP+3D screening ===");
        sVivo.printSeatMap();
        List<TicketPurchase> pVip = chain.buyTickets(sVivo, c1, "B1");
        System.out.println("Bought VIP+3D: seat=B1 | price=" + pVip.get(0).price());
    }
}
