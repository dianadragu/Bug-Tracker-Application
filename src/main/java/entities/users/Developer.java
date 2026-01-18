package entities.users;

import entities.milestones.Milestone;
import entities.tickets.Ticket;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class Developer extends User{
    private String hireDate;
    private ExpertiseArea expertiseArea;
    private DevSeniority seniority;
    @Builder.Default
    private List<Ticket> assignedTickets = new ArrayList<>();
    @Builder.Default
    private List<Ticket> lostTickets = new ArrayList<>();
    @Builder.Default
    private List<String> notifications = new ArrayList<>();
    @Builder.Default
    private double performanceScore = 0;

    public void updateAssignedTicketInMilestone(int ticketId) {
        for (Milestone milestone : getMilestones()) {
            if(milestone.getTickets().contains(ticketId)){
                milestone.getRepartition().get(getUsername()).add(ticketId);
            }
        }
    }

    public void removeAssignedTicketFromMilestone(int ticketId) {
        for (Milestone milestone : getMilestones()) {
            if(milestone.getTickets().contains(ticketId)){
                milestone.getRepartition().get(getUsername()).remove(Integer.valueOf(ticketId));
            }
        }
    }


}
