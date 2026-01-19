package entities.tickets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import reports.TicketReportVisitor;

@Getter
@Setter
@SuperBuilder
public class UiFeedback extends Ticket{
    private String uiElementsId;
    private BusinessValue businessValue;
    private Integer usabilityScore;
    private String screenshotUrl;
    private String suggestedFix;

    @Override
    public double accept(TicketReportVisitor visitor) {
        return visitor.visit(this);
    }
}
