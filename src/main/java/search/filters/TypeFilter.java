package search.filters;

import entities.tickets.Ticket;
import entities.tickets.TicketType;

public class TypeFilter implements SearchStrategy<Ticket> {
    private TicketType type;

    public TypeFilter(TicketType type) {
        this.type = type;
    }

    @Override
    public boolean passFilter(Ticket entity) {
        return type.equals(entity.getType());
    }
}
