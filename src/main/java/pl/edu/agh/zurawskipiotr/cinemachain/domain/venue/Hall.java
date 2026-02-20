package pl.edu.agh.zurawskipiotr.cinemachain.domain.venue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hall {
    private final String name;
    private final List<Seat> seats = new ArrayList<>();
    private Cinema cinema;

    public Hall(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    public Cinema getCinema() {
        return cinema;
    }

    // ustawiane tylko przez Cinema przy rejestracji sali
    void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
    }

}
