package database;


import entities.milestones.Milestone;
import entities.tickets.Ticket;
import entities.users.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AppDatabase {
    private static final AppDatabase INSTANCE = new AppDatabase();

    private List<User> users;
    private List<Ticket> createdTickets;
    private List<Milestone> createdMilestones;
    private WorkflowPhase workflowPhase;
    private LocalDate startDateCurrentPhase;
    private Map<String, List<Ticket>> reportedTickets;

    private AppDatabase() {
        this.users = new ArrayList<>();
        this.createdTickets = new ArrayList<>();
        this.createdMilestones = new ArrayList<>();
        this.workflowPhase = WorkflowPhase.TESTING;
        this.reportedTickets = new HashMap<>();
    }

    public static AppDatabase getInstance() {
        return INSTANCE;
    }
}
