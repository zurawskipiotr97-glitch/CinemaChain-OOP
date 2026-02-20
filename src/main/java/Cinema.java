import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<Hall> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public List<Screening> getScreenings() {
        return Collections.unmodifiableList(screenings);
    }

    public void addHall(Hall hall) { rooms.add(hall); }
    public void addScreening(Screening screening) { screenings.add(screening); }
    public void removeHall(Hall hall) { rooms.remove(hall); }
    public void removeScreening(Screening screening) { screenings.remove(screening); }

    // =========================================================
    // REPERTUAR NA NAJBLIŻSZY TYDZIEŃ
    // =========================================================

    /**
     * Najbliższy tydzień: od dzisiaj 00:00 do (dzisiaj+8) 00:00,
     * czyli obejmuje dni: dziś..dziś+7 (8 dni kalendarzowych).
     */
    public List<Screening> getProgrammeForNextWeek() {
        LocalDate today = LocalDate.now();
        LocalDateTime fromInclusive = today.atStartOfDay();
        LocalDateTime toExclusive = today.plusDays(8).atStartOfDay();
        return getProgrammeBetween(fromInclusive, toExclusive);
    }

    /**
     * Przedział [fromInclusive, toExclusive)
     */
    public List<Screening> getProgrammeBetween(LocalDateTime fromInclusive, LocalDateTime toExclusive) {
        List<Screening> result = new ArrayList<>();
        for (Screening s : screenings) {
            LocalDateTime t = s.getStartTime();
            if ((t.isEqual(fromInclusive) || t.isAfter(fromInclusive)) && t.isBefore(toExclusive)) {
                result.add(s);
            }
        }
        result.sort(Comparator.comparing(Screening::getStartTime));
        return result;
    }

    /**
     * Wymóg z README: repertuar na najbliższy tydzień.
     */
    public void printProgramme() {
        List<Screening> week = getProgrammeForNextWeek();

        System.out.println("Repertuar kina: " + name + " (" + address + ")");
        System.out.println("Zakres: " + LocalDate.now() + " -> " + LocalDate.now().plusDays(7));
        System.out.println();

        if (week.isEmpty()) {
            System.out.println("Brak seansów w najbliższym tygodniu.");
            return;
        }

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        Map<LocalDate, List<Screening>> byDate = new LinkedHashMap<>();
        for (Screening s : week) {
            LocalDate d = s.getStartTime().toLocalDate();
            byDate.computeIfAbsent(d, k -> new ArrayList<>()).add(s);
        }

        for (Map.Entry<LocalDate, List<Screening>> entry : byDate.entrySet()) {
            LocalDate date = entry.getKey();
            System.out.println("=== " + date + " ===");
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
}