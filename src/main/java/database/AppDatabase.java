package database;


import entities.milestones.Milestone;
import entities.tickets.Ticket;
import entities.users.User;
import lombok.Getter;
import lombok.Setter;
import utils.DatesManagement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AppDatabase {
    private static final AppDatabase INSTANCE = new AppDatabase();
    private static final int TESTING_PHASE_TIME = 12;

    private List<User> users;
    private List<Ticket> createdTickets;
    private List<Milestone> createdMilestones;
    private WorkflowPhase workflowPhase;
    private LocalDate startTestingPhase;
    private Map<String, List<Ticket>> reportedTickets;

    private int availableTicketId = 0;

    private AppDatabase() {
        this.users = new ArrayList<>();
        this.createdTickets = new ArrayList<>();
        this.createdMilestones = new ArrayList<>();
        this.workflowPhase = WorkflowPhase.TESTING;
        this.reportedTickets = new HashMap<>();
        this.startTestingPhase = null;
    }

    public static AppDatabase getInstance() {
        return INSTANCE;
    }

    public void clearDatabase() {
        users.clear();
        createdTickets.clear();
        createdMilestones.clear();
        reportedTickets.clear();
        startTestingPhase = null;
        workflowPhase = WorkflowPhase.TESTING;
        availableTicketId = 0;
    }

    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void addReportedTickets(String username, Ticket ticket) {
        if (reportedTickets.get(username) == null) {
            reportedTickets.put(username, new ArrayList<>());
        }
        reportedTickets.get(username).add(ticket);
    }

    public void updateWorkflowPhase(LocalDate currentTime) {
        if (workflowPhase == WorkflowPhase.TESTING) {
            if (startTestingPhase == null) {
                startTestingPhase = currentTime;
            } else {
                if (DatesManagement.getDaysPassed(startTestingPhase, currentTime) > TESTING_PHASE_TIME) {
                    workflowPhase = WorkflowPhase.DEVELOPMENT;
                    startTestingPhase = null;
                }
            }
        }
    }

    public Ticket getTicketById(int ticketId) {
        for (Ticket ticket : createdTickets) {
            if (ticket.getId() == ticketId) {
                return ticket;
            }
        }
        return null;
    }


    public Milestone getMilestoneByName(String milestoneName) {
        for (Milestone milestone : createdMilestones) {
            if (milestone.getName().equals(milestoneName)) {
                return milestone;
            }
        }
        return null;
    }

    public String assignedToOtherMilestone(int ticketId) {
        for (Milestone milestone : createdMilestones) {
            if (milestone.getTickets().contains(ticketId)) {
                return milestone.getName();
            }
        }
        return null;
    }

    public void updateMilestones(LocalDate currentDate) {
        for (Milestone milestone : createdMilestones) {
            milestone.update(currentDate);
        }
    }
}
