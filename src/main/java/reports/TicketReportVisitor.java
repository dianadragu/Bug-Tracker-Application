package reports;

import entities.tickets.Bug;
import entities.tickets.FeatureRequest;
import entities.tickets.Ticket;
import entities.tickets.UiFeedback;

public interface TicketReportVisitor {
    public double visit(Bug bug);
    public double visit(FeatureRequest featureRequest);
    public double visit(UiFeedback uiFeedback);
}
