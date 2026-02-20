package pl.edu.agh.zurawskipiotr.cinemachain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hall {
    private final String name;
    private final List<Seat> seats = new ArrayList<>();

    public Hall(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
    }

}
