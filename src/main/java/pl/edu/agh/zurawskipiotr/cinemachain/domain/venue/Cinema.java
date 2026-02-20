package pl.edu.agh.zurawskipiotr.cinemachain.domain.venue;

import pl.edu.agh.zurawskipiotr.cinemachain.domain.screening.Screening;

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
        if (hall == null) throw new IllegalArgumentException("hall is required");
        boolean exists = rooms.stream().anyMatch(h -> h.getName().equalsIgnoreCase(hall.getName()));
        if (exists) {
            throw new IllegalStateException("pl.edu.agh.zurawskipiotr.cinemachain.model.Hall with name already exists: " + hall.getName());
        }
        rooms.add(hall);
        hall.setCinema(this);
    }

    public void addScreening(Screening screening) {
        if (screening == null) throw new IllegalArgumentException("screening is required");
        if (!rooms.contains(screening.getHall())) {
            throw new IllegalStateException("pl.edu.agh.zurawskipiotr.cinemachain.model.Screening hall is not registered in this cinema: " + screening.getHall().getName());
        }
        screenings.add(screening);
    }

    public void removeHall(Hall hall) {
        if (hall == null) return;
        rooms.remove(hall);
        if (hall.getCinema() == this) hall.setCinema(null);
    }

    public void removeScreening(Screening screening) {
        if (screening == null) return;
        screenings.remove(screening);
    }

    public void printHalls() {
        System.out.println("Halls in cinema: " + name + " (" + address + ")");
        if (rooms.isEmpty()) {
            System.out.println("  <none>");
            return;
        }
        for (Hall hall : rooms) {
            System.out.println("  - " + hall.getName() + " | seats: " + hall.getSeats().size());
        }
    }

    public void printScreenings() {
        System.out.println("Screenings in cinema: " + name + " (" + address + ")");
        if (screenings.isEmpty()) {
            System.out.println("  <none>");
            return;
        }
        screenings.stream()
                .sorted(Comparator.comparing(Screening::getStartTime))
                .forEach(s -> {
                    System.out.println("  - " + s.getMovie().title()
                            + " | " + s.getStartTime()
                            + " | hall=" + s.getHall().getName()
                            + (s.isVip() ? " | VIP" : "")
                            + (s.isThreeD() ? " | 3D" : ""));
                });
    }

    public List<Screening> getProgrammeForNextWeek() {
        LocalDate today = LocalDate.now();
        LocalDateTime fromInclusive = today.atStartOfDay();
        LocalDateTime toExclusive = today.plusDays(7).atStartOfDay();
        return getProgrammeBetween(fromInclusive, toExclusive);
    }

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

    public void printProgramme() {
        List<Screening> week = getProgrammeForNextWeek();

        System.out.println("Repertuar kina: " + name + " (" + address + ")");
        System.out.println("Zakres: " + LocalDate.now() + " -> " + LocalDate.now().plusDays(6));
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
