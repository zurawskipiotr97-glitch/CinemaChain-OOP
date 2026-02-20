package pl.edu.agh.zurawskipiotr.cinemachain.pricing;

import pl.edu.agh.zurawskipiotr.cinemachain.domain.venue.Seat;

import java.math.BigDecimal;

/**
 * Pricing strategy for a single ticket.
 *
 * <p>Keep this interface free of dependencies on high-level aggregates (e.g. Screening),
 * and pass only the data required to compute the price. This reduces coupling and
 * makes pricing policies easier to test and reuse.</p>
 */
public interface PricingPolicy {
    BigDecimal calculatePrice(Seat seat, boolean vipScreening, boolean threeD);
}
