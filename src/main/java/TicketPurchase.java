import java.math.BigDecimal;

public record TicketPurchase(Ticket ticket, BigDecimal price) {}