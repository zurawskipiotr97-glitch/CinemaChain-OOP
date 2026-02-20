public class Seat {
    private final String row;
    private final String seat;
    private final String code;
    private final String category;

    public Seat(String row, String seat, String category) {
        this.row = row;
        this.seat = seat;
        this.code = row+seat;
        this.category = category;
    }

    public String getRow() {
        return row;
    }

    public String getSeat() {
        return seat;
    }

    public String getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }
}
