package entities.tickets;

import com.fasterxml.jackson.annotation.JsonInclude;
import entities.users.ExpertiseArea;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public abstract class Ticket {
    private Integer id;
    private String type;
    private String title;
    private TicketPriority businessPriority;
    private ExpertiseArea expertiseArea;
    private String description;
    private String reportedBy;
    private TicketStatus status = TicketStatus.OPEN;
    private List<String> comments;
    private String solvedAt;
    private String assignedAt;
    private String assignedTo;
    private String createdAt;

    public void updatePriority() {
        switch (businessPriority) {
            case LOW:
                businessPriority = TicketPriority.MEDIUM;
                break;
            case MEDIUM:
                businessPriority = TicketPriority.HIGH;
                break;
            case HIGH:
                businessPriority = TicketPriority.CRITICAL;
                break;
            case CRITICAL:
                break;
            default:
                throw new IllegalArgumentException("Invalid business priority");
        }
    }

    public void changePriorityToCritical() {
        businessPriority = TicketPriority.CRITICAL;
    }
}
