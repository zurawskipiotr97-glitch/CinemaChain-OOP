import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CinemaChain {

    private String chainName;
    private final Map<String, Cinema> cinemas = new HashMap<>();
    //email,
    private final Map<String, Customer> customers = new HashMap<>();

    public CinemaChain(String chainName) {
        this.chainName = chainName;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public Map<String, Cinema> getCinemas() {
        return cinemas;
    }

    public Map<String, Customer> getCustomers() {
        return customers;
    }

    public void addCinema(Cinema cinema) {
        cinemas.put(cinema.getId(), cinema);
    }
}
