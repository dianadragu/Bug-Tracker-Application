package entities.tickets.observers;

import database.AppDatabase;
import entities.milestones.Milestone;
import entities.observer.Observer;
import entities.tickets.Ticket;
import entities.tickets.TicketStatus;

public class NotifyMilestone implements Observer<TicketNotification> {
    @Override
    public void update(TicketNotification notification) {
        Ticket ticket = notification.getTicket();
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            AppDatabase database = AppDatabase.getInstance();
            for (Milestone milestone: database.getCreatedMilestones()) {
                if (milestone.getTickets().contains(ticket.getId())) {
                    milestone.checkUponCompletion(ticket.getId());
                }
            }
        }
    }
}
