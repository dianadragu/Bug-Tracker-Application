package search.filters;

import entities.tickets.Ticket;

import java.time.LocalDate;

public class CreatedBeforeFilter implements SearchStrategy<Ticket> {
    private final LocalDate createdBefore;

    public CreatedBeforeFilter(LocalDate createdBefore) {
        this.createdBefore = createdBefore;
    }

    @Override
    public boolean passFilter(Ticket entity) {
        LocalDate ticketDate = LocalDate.parse(entity.getCreatedAt());
        return ticketDate.isBefore(createdBefore);
    }
}