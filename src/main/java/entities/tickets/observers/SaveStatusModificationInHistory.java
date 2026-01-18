package entities.tickets.observers;

import entities.observer.Observer;
import entities.tickets.TicketHistory;

public class SaveStatusModificationInHistory implements Observer<TicketNotification> {
    @Override
    public void update(TicketNotification ticketNotification) {
        TicketHistory ticketHistory = new TicketHistory();
        ticketHistory.saveStatusModification(ticketNotification.getUsername(), ticketNotification.getTimestamp(),
                                            ticketNotification.getOldStatus(), ticketNotification.getNewStatus());

        ticketNotification.getTicket().getTicketHistory().add(ticketHistory);
    }
}
