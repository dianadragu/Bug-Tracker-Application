package entities.milestones;

import database.AppDatabase;
import entities.milestones.observers.*;
import entities.observer.Subject;
import entities.tickets.Ticket;
import entities.tickets.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import utils.DatesManagement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
public class Milestone extends Subject<MilestoneNotification> {
    private final static int TICKET_PRIORITY_DURATION = 3;
    private String name;
    private List<String> blockingFor;
    private LocalDate dueDate;
    private List<Integer> tickets;
    private List<String> assignedDevs;
    private LocalDate createdAt;
    private String createdBy;
    private MilestoneStatus status;
    private int daysUntilDue;
    private int overdueBy;
    private List<Integer> openTickets = new ArrayList<>();
    private List<Integer> closedTickets = new ArrayList<>();
    private boolean blocked;
    private Map<String, List<Integer>> repartition;
    private LocalDate updatedAt;

    public Milestone(String name, String dueDate, List<String> blockingFor, List<Integer> tickets, List<String> assignedDevs) {
        this.name = name;
        this.blockingFor = blockingFor;
        this.dueDate = LocalDate.parse(dueDate);
        this.tickets = tickets;
        this.assignedDevs = assignedDevs;
        this.status = MilestoneStatus.ACTIVE;
        this.daysUntilDue = 0;
        this.overdueBy = 0;
        this.openTickets = new ArrayList<>();
        this.closedTickets = new ArrayList<>();
        this.repartition = new HashMap<>();
        this.blocked = false;

        loadRepartitionMap();

        addObserver(new MilestoneCompleted());
        addObserver(new MilestoneCreated());
        addObserver(new MilestoneDueDateIsTomorrow());
        addObserver(new MilestonePassedDueDate());
    }

    public void sendNotificationForCreatedMilestone(LocalDate currentTimestamp) {
        MilestoneNotification notification = new MilestoneNotification();
        notification.setMilestone(this);
        notification.setMilestoneName(name);
        notification.setDueDate(dueDate);
        notification.setCurrentDate(currentTimestamp);
        notifyObservers(notification);
    }

    public Double calculateCompletionPercentage() {
        List<Ticket> tickets = getAssignedTickets();
        Double totalClosedTickets = 0.0;

        for (Ticket ticket : tickets) {
            if(ticket != null && ticket.getStatus() == TicketStatus.CLOSED) {
                totalClosedTickets++;
            }
        }

        return totalClosedTickets / tickets.size();
    }

    public List<Ticket> getAssignedTickets() {
        List<Ticket> assignedTickets = new ArrayList<>();
        AppDatabase database = AppDatabase.getInstance();

        for (Integer ticketId : tickets) {
            assignedTickets.add(database.getTicketById(ticketId));
        }

        return assignedTickets;
    }

    public void updateDates(LocalDate currentDate) {
        if (currentDate.isBefore(dueDate)) {
            daysUntilDue = DatesManagement.getDaysPassed(currentDate, dueDate);
            overdueBy = 0;
        } else {
            overdueBy = DatesManagement.getDaysPassed(dueDate, currentDate);
            daysUntilDue = 0;
        }
    }

    public void updateOpenTickets() {
        openTickets.clear();
        AppDatabase database = AppDatabase.getInstance();
        for (Integer ticketId : tickets) {
            if (database.getTicketById(ticketId).getStatus() != TicketStatus.CLOSED &&
            !openTickets.contains(ticketId)) {
                openTickets.add(ticketId);
            }
        }
    }

    public void updateClosedTickets() {
        closedTickets.clear();
        AppDatabase database = AppDatabase.getInstance();
        for (Integer ticketId : tickets) {
            if(database.getTicketById(ticketId).getStatus() == TicketStatus.CLOSED &&
            !closedTickets.contains(ticketId)) {
                closedTickets.add(ticketId);
            }
        }
    }

    public void blockMilestones() {
        AppDatabase database = AppDatabase.getInstance();
        for (String milestone : blockingFor) {
            Milestone blockedMilestone = database.getMilestoneByName(milestone);
            blockedMilestone.setBlocked(true);
        }
    }

    public void unblockYourself() {
        blocked = false;
        MilestoneNotification notification = new MilestoneNotification();
        notification.setMilestone(this);
        notification.setMilestoneName(name);
        notification.setDueDate(dueDate);
        notification.setCanBeUnblocked(true);
        notifyObservers(notification);
    }

    public void checkUponCompletion(Integer ticketId) {
        AppDatabase database = AppDatabase.getInstance();
        boolean allTicketsClosed = true;
        for (Integer id : tickets) {
            Ticket ticket = database.getTicketById(id);
            if (ticket.getStatus() != TicketStatus.CLOSED) {
                allTicketsClosed = false;
            }
        }

        if (allTicketsClosed) {
            status = MilestoneStatus.COMPLETED;
            MilestoneNotification notification = new MilestoneNotification();
            notification.setMilestone(this);
            notification.setMilestoneName(name);
            notification.setTicketId(ticketId);
            notifyObservers(notification);
        }
    }

    public void loadRepartitionMap() {
        for (String devName : assignedDevs) {
            repartition.put(devName, new ArrayList<Integer>());
        }
    }

    public void update(LocalDate currentDate) {
        if (status == MilestoneStatus.ACTIVE) {
            updateDates(currentDate);
        }

        updateOpenTickets();
        updateClosedTickets();

        if (!blocked) {
            AppDatabase database = AppDatabase.getInstance();
            int daysPassed = DatesManagement.getDaysPassed(updatedAt, currentDate);
            if (daysPassed >= TICKET_PRIORITY_DURATION) {
                updatedAt = currentDate.minusDays(daysPassed % TICKET_PRIORITY_DURATION);
                int nrOfTimesToUpdate = daysPassed / TICKET_PRIORITY_DURATION;
                while (nrOfTimesToUpdate > 0) {
                    for (Integer ticketId : tickets) {
                        database.getTicketById(ticketId).updatePriority();
                    }
                    nrOfTimesToUpdate--;
                }
            }

            if (overdueBy > 0) {
                for (Integer ticketId : tickets) {
                    database.getTicketById(ticketId).changePriorityToCritical();
                }
            }

            MilestoneNotification notification = new MilestoneNotification();
            notification.setMilestone(this);
            notification.setMilestoneName(name);
            notification.setDueDate(dueDate);
            notification.setCurrentDate(currentDate);
            notifyObservers(notification);
        }
    }
}
