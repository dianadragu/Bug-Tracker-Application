package search.filters;

import entities.tickets.Ticket;

import java.time.LocalDate;

public class CreatedAfterFilter implements SearchStrategy<Ticket> {
    private final LocalDate createdAfter;

    public CreatedAfterFilter(LocalDate createdAfter) {
        this.createdAfter = createdAfter;
    }

    @Override
    public boolean passFilter(Ticket entity) {
        LocalDate ticketDate = LocalDate.parse(entity.getCreatedAt());
        return ticketDate.isAfter(createdAfter);
    }
}