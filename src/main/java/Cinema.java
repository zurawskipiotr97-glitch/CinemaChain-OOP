import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Cinema {
    private final String id;
    private final List<Hall> rooms = new ArrayList<>();
    private final List<Screening> screenings = new ArrayList<>();

    private String name;
    private String address;

    public Cinema(String name, String address) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Hall> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public List<Screening> getScreenings() {
        return Collections.unmodifiableList(screenings);
    }

    public void addHall(Hall hall) {
        rooms.add(hall);
    }

    public void addScreening(Screening screening) {
        screenings.add(screening);
    }

    public void removeHall(Hall hall) {
        rooms.remove(hall);
    }

    public void removeScreening(Screening screening) {
        screenings.remove(screening);
    }

    public void printProgramme() {
        for (Screening screening : screenings) {
//            TODO
        }

    }
}
