package entities.tickets;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
public class TicketHistory {
    private String action;
    private String by;
    private String timestamp;
    private String from;
    private String to;
    private String milestone;

    public TicketHistory saveAssignment(String username, String timestamp) {
        action = "ASSIGNED";
        by = username;
        this.timestamp = timestamp;
        return this;
    }

    public TicketHistory saveDeAssignment(String username, String timestamp) {
        action = "DE-ASSIGNED";
        by = username;
        this.timestamp = timestamp;
        return this;
    }

    public TicketHistory saveStatusModification(String username, String timestamp, TicketStatus oldStatus, TicketStatus newStatus ) {
        action = "STATUS_CHANGED";
        from = oldStatus.toString();
        to = newStatus.toString();
        by = username;
        this.timestamp = timestamp;
        return this;
    }

    public TicketHistory saveMilestoneAddition(String username, String timestamp, String milestone) {
        action = "ADDED_TO_MILESTONE";
        this.milestone = milestone;
        by = username;
        this.timestamp = timestamp;
        return this;
    }

    public TicketHistory saveTicketRemoval(String username, String timestamp, String dev) {
        action = "ADDED_TO_MILESTONE";
        by = username;
        from = dev;
        return this;
    }


}
