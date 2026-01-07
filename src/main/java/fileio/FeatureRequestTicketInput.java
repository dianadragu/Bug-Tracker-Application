package fileio;

import entities.tickets.TicketLoaderVisitor;
import entities.tickets.Ticket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureRequestTicketInput extends ReportedTicketParamsInput{
    private String businessValue;
    private String customerDemand;

    @Override
    public Ticket accept(TicketLoaderVisitor visitor) {
        return visitor.createTicket(this);
    }
}
