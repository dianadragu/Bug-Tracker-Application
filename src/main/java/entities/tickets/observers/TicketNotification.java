package entities.tickets.observers;

import entities.tickets.Ticket;
import entities.tickets.TicketStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TicketNotification {
    Ticket ticket;
    TicketStatus oldStatus;
    TicketStatus newStatus;
    String timestamp;
    String username;

    public TicketNotification(Ticket ticket, TicketStatus oldStatus, TicketStatus newStatus, String timestamp, String username) {
        this.ticket = ticket;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.timestamp = timestamp;
        this.username = username;
    }
}
