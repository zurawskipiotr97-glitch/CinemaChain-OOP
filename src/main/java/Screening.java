import java.util.Map;

public class Screening {
        private Movie movie;
        private Hall hall;

        private boolean isVip;
        private boolean isThreeD;

    public double calculatePrice(Seat seat) {
        double base = priceByCategory.get(seat.getCategory());

        if (isThreeD) {
            base += 5;
        }

        if (isVip) {
            base += 10;
        }

        return base;
    }

}
