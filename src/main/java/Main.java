import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // ===== Sieć =====
        CinemaChain Multikino = new CinemaChain("Mulitkino");

        // ===== Kina =====
        Cinema tarasy = new Cinema("Super Tarasy", "ul.Akadamicka 5");
        Cinema vivo = new Cinema("Vivo", "ul.Chopina 42");
        Cinema starowka = new Cinema("Starówka", "ul. Rynek 25/26");

        Multikino.addCinema(tarasy);
        Multikino.addCinema(vivo);
        Multikino.addCinema(starowka);

        // ===== FILMY =====
        Movie avatar = new Movie("Avatar: Istota Wody", 162, List.of(Genre.SCI_FI), 12);
        Movie oppenheimer = new Movie("Oppenheimer", 180, List.of(Genre.BIOGRAPHY, Genre.DRAMA, Genre.HISTORICAL), 15);
        Movie barbie = new Movie("Barbie", 114, List.of(Genre.COMEDY), 7);
        Movie dune = new Movie("Diuna: Część II", 166, List.of(Genre.SCI_FI), 12);
        Movie johnWick = new Movie("John Wick 4", 169, List.of(Genre.ACTION), 16);
        Movie wish = new Movie("Życzenie", 95, List.of(Genre.ANIMATION, Genre.FAMILY), 0);

        // ===== DODANIE FILMÓW DO SIECI =====
        Multikino.addMovie(avatar);
        Multikino.addMovie(oppenheimer);
        Multikino.addMovie(dune);
        Multikino.addMovie(wish);
        Multikino.addMovie(barbie);
        Multikino.addMovie(johnWick);

        // ===== DODANIE UŻYTKOWIKÓW =====
        Customer c1 = new Customer("Jan", "Kowalski", "jan.kowalski@mail.com");
        Customer c2 = new Customer("Anna", "Nowak", "anna.nowak@mail.com");
        Customer c3 = new Customer("Piotr", "Zieliński", "piotr.zielinski@mail.com");

        Multikino.registerCustomer(c1);
        Multikino.registerCustomer(c2);
        Multikino.registerCustomer(c3);

        // ===== SALE + MIEJSCA =====
        Hall hall1 = new Hall("Sala 1");
        for (int i = 1; i <= 10; i++) hall1.addSeat(new Seat("A", i, SeatCategory.STANDARD));
        for (int i = 1; i <= 5; i++) hall1.addSeat(new Seat("B", i, SeatCategory.VIP));

        tarasy.addHall(hall1);

// ===== SEANS (dziś + 2h, żeby był w "najbliższym tygodniu") =====
        Screening screening = new Screening(
                avatar,
                hall1,
                false,   // isVip screening
                true,    // 3D
                java.time.LocalDateTime.now().plusHours(2)
        );
        tarasy.addScreening(screening);

// ===== REPERTUAR =====
        Multikino.printProgramme();   // sieć
        tarasy.printProgramme();      // jedno kino

// ===== KLIENT kupuje bilety i sprawdza swoje =====
        List<TicketPurchase> p1 = screening.buyTickets(Multikino, c1, "A1", "A2");
        c1.printOwnTickets();

// ===== GOŚĆ kupuje bilet i sprawdza po kodzie (CinemaChain) =====
        List<TicketPurchase> p2 = screening.buyTickets(Multikino, "A3");
        String guestCode = p2.get(0).ticket().getCode();
        System.out.println("Kod biletu gościa: " + guestCode);

        Ticket found = Multikino.findTicketByCode(guestCode);
        System.out.println("Znaleziony bilet: " + found.getCode()
                + " | " + found.getScreening().getMovie().title()
                + " | " + found.getScreening().getStartTime()
                + " | miejsce: " + found.getSeat().getCode());

// ===== GOŚĆ rezerwuje tokenem -> kupuje -> sprawdza =====
        String token = screening.reservePlaces("A4", "A5");
        List<TicketPurchase> p3 = screening.buyTickets(Multikino, token, "A4", "A5");
        String codeFromReservation = p3.get(0).ticket().getCode();
        System.out.println("Kod biletu (gość po rezerwacji): " + codeFromReservation);
        System.out.println("Wyszukiwanie w chain: " + (Multikino.findTicketByCode(codeFromReservation) != null));


    }
}
