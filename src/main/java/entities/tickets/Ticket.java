package entities.tickets;

import entities.users.ExpertiseArea;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class Ticket {
    private int id;
    private String type;
    private String title;
    private TicketPriority businessPriority;
    private TicketStatus status;
    private ExpertiseArea expertiseArea;
    private String description;
    private String reportedBy;
}
