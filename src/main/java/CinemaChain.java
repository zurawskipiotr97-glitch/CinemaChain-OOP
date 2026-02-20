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

}
