package pl.edu.agh.zurawskipiotr.cinemachain.domain.sales;

import java.math.BigDecimal;

public record TicketPurchase(Ticket ticket, BigDecimal price) {}