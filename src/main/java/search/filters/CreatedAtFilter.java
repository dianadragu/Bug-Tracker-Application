package search.filters;
import entities.tickets.Ticket;

import java.time.LocalDate;

public class CreatedAtFilter implements SearchStrategy<Ticket> {
    private final LocalDate createdAt;

    public CreatedAtFilter(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean passFilter(Ticket entity) {
        return createdAt.equals(LocalDate.parse(entity.getCreatedAt()));
    }
}