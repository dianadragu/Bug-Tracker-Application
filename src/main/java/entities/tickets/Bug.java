package entities.tickets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import reports.TicketReportVisitor;

@Getter
@Setter
@SuperBuilder
public class Bug extends Ticket{
    private String expectedBehavior;
    private String actualBehavior;
    private BugFrequency frequency;
    private BugSeverity severity;
    private String environment;
    private Integer errorCode;

    @Override
    public double accept(TicketReportVisitor visitor) {
        return visitor.visit(this);
    }
}
