import java.time.Duration;
import java.util.List;

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


    }
}
