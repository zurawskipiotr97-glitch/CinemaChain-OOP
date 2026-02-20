package pl.edu.agh.zurawskipiotr.cinemachain.pricing;

import pl.edu.agh.zurawskipiotr.cinemachain.domain.venue.Seat;
import pl.edu.agh.zurawskipiotr.cinemachain.domain.venue.SeatCategory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

public class DefaultPricingPolicy implements PricingPolicy {

    private final Map<SeatCategory, BigDecimal> basePrices;
    private final BigDecimal threeDSurcharge;
    private final BigDecimal vipScreeningSurcharge;

    public DefaultPricingPolicy(
            Map<SeatCategory, BigDecimal> basePrices,
            BigDecimal threeDSurcharge,
            BigDecimal vipScreeningSurcharge
    ) {
        this.basePrices = new EnumMap<>(basePrices);
        this.threeDSurcharge = threeDSurcharge;
        this.vipScreeningSurcharge = vipScreeningSurcharge;
    }

    public static DefaultPricingPolicy defaultPolicy() {
        Map<SeatCategory, BigDecimal> bases = new EnumMap<>(SeatCategory.class);

        bases.put(SeatCategory.STANDARD, BigDecimal.valueOf(30));
        bases.put(SeatCategory.VIP, BigDecimal.valueOf(45));
        bases.put(SeatCategory.PROMO, BigDecimal.valueOf(25));
        bases.put(SeatCategory.SUPER_PROMO, BigDecimal.valueOf(20));

        return new DefaultPricingPolicy(
                bases,
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(10)
        );
    }

    @Override
    public BigDecimal calculatePrice(Seat seat, boolean vipScreening, boolean threeD) {
        BigDecimal price = basePrices.get(seat.category());

        if (price == null) {
            throw new IllegalStateException("No base price for category: " + seat.category());
        }

        if (threeD) {
            price = price.add(threeDSurcharge);
        }

        if (vipScreening) {
            price = price.add(vipScreeningSurcharge);
        }

        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
