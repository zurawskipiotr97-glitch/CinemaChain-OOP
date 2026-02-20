import java.util.UUID;

public class Ticket {
    private final String code;
    private final Screening screening;
    private final Seat seat;
    private final Customer owner;       // null jeśli guest

    public Ticket(Screening screening, Seat seat, Customer owner) {
        this.code = UUID.randomUUID().toString();
        this.screening = screening;
        this.seat = seat;
        this.owner = owner;
    }

    public String getCode() {
        return code;
    }

    public Screening getScreening() {
        return screening;
    }

    public Seat getSeat() {
        return seat;
    }

    public Customer getOwner() {
        return owner;
    }
}