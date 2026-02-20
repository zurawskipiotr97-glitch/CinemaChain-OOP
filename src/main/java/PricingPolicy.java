import java.math.BigDecimal;

public interface PricingPolicy {
    BigDecimal calculatePrice(Screening screening, Seat seat);
}