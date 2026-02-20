package pl.edu.agh.zurawskipiotr.cinemachain.pricing;

import pl.edu.agh.zurawskipiotr.cinemachain.domain.venue.Seat;

import java.math.BigDecimal;

//Pricing strategy for a single ticket.

public interface PricingPolicy {
    BigDecimal calculatePrice(Seat seat, boolean vipScreening, boolean threeD);
}
