package entities.tickets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import reports.TicketReportVisitor;

@Getter
@Setter
@SuperBuilder
public class FeatureRequest extends Ticket{
    private BusinessValue businessValue;
    private FeatureCustomerDemand customerDemand;

    @Override
    public double accept(TicketReportVisitor visitor) {
        return visitor.visit(this);
    }
}
