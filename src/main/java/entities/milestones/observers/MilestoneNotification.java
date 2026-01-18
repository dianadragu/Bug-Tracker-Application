package entities.milestones.observers;

import entities.milestones.Milestone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class MilestoneNotification {
    Milestone milestone;
    String milestoneName;
    LocalDate currentDate;
    LocalDate dueDate;
    Integer ticketId;
    Boolean canBeUnblocked;
}
