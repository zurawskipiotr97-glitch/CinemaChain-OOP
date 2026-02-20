import java.time.Duration;

public class Main {
    static void main() {
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
        Movie avatar = new Movie("Avatar: Istota Wody", Duration.ofMinutes(162), "Sci-Fi", 12);
        Movie oppenheimer = new Movie("Oppenheimer", Duration.ofMinutes(180), "Biograficzny", 15);
        Movie barbie = new Movie("Barbie", Duration.ofMinutes(114), "Komedia", 7);
        Movie dune = new Movie("Diuna: Część II", Duration.ofMinutes(166), "Sci-Fi", 12);
        Movie johnWick = new Movie("John Wick 4", Duration.ofMinutes(169), "Akcja", 16);
        Movie wish = new Movie("Życzenie", Duration.ofMinutes(95), "Animacja", 0);

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


    }
}
