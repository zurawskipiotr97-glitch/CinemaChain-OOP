import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CinemaChain {
    private final String id;
    private final Map<String, Cinema> cinemas = new HashMap<>();
    private final List<Movie> movies = new ArrayList<>();
    private final Map<String, Customer> customers = new HashMap<>();
    private final Map<String, Ticket> ticketsByCode = new HashMap<>();

    private String chainName;

    public CinemaChain(String chainName) {
        this.id = UUID.randomUUID().toString();
        this.chainName = chainName;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public Map<String, Cinema> getCinemas() {
        return Collections.unmodifiableMap(cinemas);
    }

    public Map<String, Customer> getCustomers() {
        return Collections.unmodifiableMap(customers);
    }

    public void addCinema(Cinema cinema) {
        cinemas.put(cinema.getId(), cinema);
    }

    public void removeCinema(Cinema cinema) {
        cinemas.remove(cinema.getId());
    }

    public void registerCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    public void unregisterCustomer(Customer customer) {
        customers.remove(customer.getId());
    }

    public List<Movie> getMovies() {
        return Collections.unmodifiableList(movies);
    }

    public Map<String, Ticket> getTicketsByCode() {
        return Collections.unmodifiableMap(ticketsByCode);
    }

    public Movie findMovieByTitle(String title) {
        return movies.stream()
                .filter(m -> m.title().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public void removeMovie(Movie movie) {
        movies.remove(movie);
    }

    public void addTicket(Ticket ticket) {
        ticketsByCode.put(ticket.getCode(), ticket);
    }

    public void removeTicket(Ticket ticket) {
        ticketsByCode.remove(ticket.getCode());
    }

    // =========================================================
    // ZAKUPY (OPCJA 2): CinemaChain sprzedaje i rejestruje bilety
    // =========================================================

    // Klient kupuje (FREE + swoje RESERVED)
    public List<TicketPurchase> buyTickets(Screening screening, Customer customer, String... seatCodes) {
        Objects.requireNonNull(screening, "screening");
        Objects.requireNonNull(customer, "customer");

        List<TicketPurchase> purchases = screening.buyTicketsForCustomer(customer, seatCodes);
        registerPurchases(purchases);
        return purchases;
    }

    // Gość kupuje bez rezerwacji (tylko FREE)
    public List<TicketPurchase> buyTicketsAsGuest(Screening screening, String... seatCodes) {
        Objects.requireNonNull(screening, "screening");

        List<TicketPurchase> purchases = screening.buyTicketsAsGuest(seatCodes);
        registerPurchases(purchases);
        return purchases;
    }

    // Gość kupuje używając tokena (FREE + tokenowe RESERVED)
    public List<TicketPurchase> buyTicketsWithToken(Screening screening, String token, String... seatCodes) {
        Objects.requireNonNull(screening, "screening");

        List<TicketPurchase> purchases = screening.buyTicketsAsGuestWithToken(token, seatCodes);
        registerPurchases(purchases);
        return purchases;
    }

    private void registerPurchases(List<TicketPurchase> purchases) {
        for (TicketPurchase tp : purchases) {
            addTicket(tp.ticket());
        }
    }

    // =========================================================
    // REPERTUAR SIECI NA NAJBLIŻSZY TYDZIEŃ
    // =========================================================

    public void printProgramme() {
        System.out.println("Repertuar sieci: " + chainName);
        System.out.println("Zakres: " + LocalDate.now() + " -> " + LocalDate.now().plusDays(6));
        System.out.println();

        if (cinemas.isEmpty()) {
            System.out.println("Brak kin w sieci.");
            return;
        }

        boolean any = false;

        for (Cinema cinema : cinemas.values()) {
            List<Screening> week = cinema.getProgrammeForNextWeek();
            if (week.isEmpty()) continue;

            any = true;

            System.out.println("##################################################");
            System.out.println("Kino: " + cinema.getName() + " (" + cinema.getAddress() + ")");
            System.out.println("##################################################");

            printProgrammeBlock(week);
            System.out.println();
        }

        if (!any) {
            System.out.println("Brak seansów w najbliższym tygodniu w całej sieci.");
        }
    }

    private void printProgrammeBlock(List<Screening> week) {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        Map<LocalDate, List<Screening>> byDate = new LinkedHashMap<>();
        for (Screening s : week) {
            LocalDate d = s.getStartTime().toLocalDate();
            byDate.computeIfAbsent(d, k -> new ArrayList<>()).add(s);
        }

        for (Map.Entry<LocalDate, List<Screening>> entry : byDate.entrySet()) {
            System.out.println("=== " + entry.getKey() + " ===");
            for (Screening s : entry.getValue()) {
                System.out.println(
                        s.getStartTime().format(timeFmt)
                                + " | " + s.getMovie().title()
                                + " | sala: " + s.getHall().getName()
                                + buildTags(s)
                );
            }
            System.out.println();
        }
    }

    private String buildTags(Screening s) {
        List<String> tags = new ArrayList<>();
        if (s.isVip()) tags.add("VIP");
        if (s.isThreeD()) tags.add("3D");
        return tags.isEmpty() ? "" : " | [" + String.join(", ", tags) + "]";
    }

    public Ticket findTicketByCode(String code) {
        if (code == null || code.isBlank()) return null;
        return ticketsByCode.get(code);
    }

    // alias
    public Ticket getTicketByCode(String code) {
        return findTicketByCode(code);
    }
}