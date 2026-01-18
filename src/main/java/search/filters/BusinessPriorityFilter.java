package search.filters;

import entities.tickets.Ticket;
import entities.tickets.TicketPriority;

public class BusinessPriorityFilter implements SearchStrategy<Ticket> {
    private TicketPriority businessPriority;

    public BusinessPriorityFilter(TicketPriority businessPriority) {
        this.businessPriority = businessPriority;
    }

    @Override
    public boolean passFilter(Ticket entity) {
        return businessPriority.equals(entity.getBusinessPriority());
    }
}
