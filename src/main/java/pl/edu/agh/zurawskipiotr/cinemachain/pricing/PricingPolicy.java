package pl.edu.agh.zurawskipiotr.cinemachain.pricing;

import pl.edu.agh.zurawskipiotr.cinemachain.model.Screening;
import pl.edu.agh.zurawskipiotr.cinemachain.model.Seat;

import java.math.BigDecimal;

public interface PricingPolicy {
    BigDecimal calculatePrice(Screening screening, Seat seat);
}