import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Customer {
    private final String id;
    private final List<Ticket> ownTickets = new ArrayList<>();

    private String firstName;
    private String lastName;
    private String email;


    public Customer(String firstName, String lastName, String email) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Ticket> getOwnTickets() {
        return Collections.unmodifiableList(ownTickets);
    }

    public void addOwnTicket(Ticket ticket) {
        this.ownTickets.add(ticket);
    }

    public void removeOwnTicket(Ticket ticket) {
        this.ownTickets.remove(ticket);
    }

    public List<String> getReservationsFor(Screening screening) {
        return screening.getReservedSeatsFor(this);
    }

    public void printOwnTickets() {
        if (ownTickets.isEmpty()) {
            System.out.println(firstName + " " + lastName + " - brak biletów.");
            return;
        }

        System.out.println("Bilety klienta: " + firstName + " " + lastName);
        for (Ticket t : ownTickets) {
            System.out.println("- " + t.getCode()
                    + " | " + t.getScreening().getMovie().title()
                    + " | " + t.getScreening().getStartTime()
                    + " | miejsce: " + t.getSeat().getCode());
        }
    }


}
