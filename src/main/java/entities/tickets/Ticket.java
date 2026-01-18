package entities.tickets;

import entities.observer.Subject;
import entities.tickets.observers.TicketNotification;
import entities.users.DevSeniority;
import entities.users.ExpertiseArea;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Getter
@Setter
@SuperBuilder
public abstract class Ticket extends Subject<TicketNotification> {
    private Integer id;
    private TicketType type;
    private String title;
    private TicketPriority businessPriority;
    private ExpertiseArea expertiseArea;
    private String description;
    private String reportedBy;
    private TicketStatus status = TicketStatus.OPEN;
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    private String solvedAt;
    private String assignedAt;
    private String assignedTo;
    private String createdAt;
    @Builder.Default
    private List<TicketHistory> ticketHistory = new LinkedList<>();

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

    public List<ExpertiseArea> getExpertiseAreas() {
        List<ExpertiseArea> expertiseAreas = new ArrayList<>();
        switch (expertiseArea) {
            case FULLSTACK:
                expertiseAreas.add(ExpertiseArea.FRONTEND);
                expertiseAreas.add(ExpertiseArea.DESIGN);
                expertiseAreas.add(ExpertiseArea.DEVOPS);
                expertiseAreas.add(ExpertiseArea.BACKEND);
                expertiseAreas.add(ExpertiseArea.DB);
            case FRONTEND, DESIGN:
                expertiseAreas.add(ExpertiseArea.FRONTEND);
                expertiseAreas.add(ExpertiseArea.DESIGN);
                expertiseAreas.add(ExpertiseArea.FULLSTACK);
                break;
            case DB:
                expertiseAreas.add(ExpertiseArea.BACKEND);
                expertiseAreas.add(ExpertiseArea.DB);
                expertiseAreas.add(ExpertiseArea.FULLSTACK);
                break;
            default:
                expertiseAreas.add(expertiseArea);
                expertiseAreas.add(ExpertiseArea.FULLSTACK);
                break;
        }
        return expertiseAreas;
    }

    public List<DevSeniority> getRequiredSeniority(){
        List<DevSeniority> seniorityList = new ArrayList<>();

        if (businessPriority == TicketPriority.CRITICAL) {
            seniorityList.add(DevSeniority.SENIOR);
            return seniorityList;
        }

        if (businessPriority == TicketPriority.HIGH) {
            seniorityList.add(DevSeniority.SENIOR);
            seniorityList.add(DevSeniority.MID);
            return seniorityList;
        }

        seniorityList.add(DevSeniority.JUNIOR);
        seniorityList.add(DevSeniority.SENIOR);
        seniorityList.add(DevSeniority.MID);
        return seniorityList;
    }

    public Comment getLastComment(String username) {
        Comment lastComment = null;
        for (Comment comment: comments) {
            if (comment.getAuthor().equals(username)) {
                lastComment = comment;
            }
        }
        return lastComment;
    }

    public void switchStatus(String timestamp, String username) {
        TicketStatus oldStatus = status;
        TicketStatus newStatus = null;

        switch (status) {
            case OPEN:
                status = TicketStatus.IN_PROGRESS;
                newStatus = TicketStatus.IN_PROGRESS;
                break;
            case IN_PROGRESS:
                status = TicketStatus.RESOLVED;
                newStatus = TicketStatus.RESOLVED;
                break;
            case RESOLVED:
                status = TicketStatus.CLOSED;
                newStatus = TicketStatus.CLOSED;
                break;
            case CLOSED:
                return;
            default:
                throw new IllegalArgumentException("Invalid status");
        }

        TicketNotification ticketNotification = new TicketNotification(this, oldStatus, newStatus, timestamp, username);
        notifyObservers(ticketNotification);

    }

    public void revertStatus(String timestamp, String username) {
        TicketStatus oldStatus = status;
        TicketStatus newStatus = null;

        switch (status) {
            case RESOLVED:
                status = TicketStatus.IN_PROGRESS;
                newStatus = TicketStatus.IN_PROGRESS;
                break;
            case CLOSED:
                status = TicketStatus.RESOLVED;
                newStatus = TicketStatus.RESOLVED;
                break;
            case IN_PROGRESS:
                status = TicketStatus.OPEN;
                newStatus = TicketStatus.OPEN;
                break;
            case OPEN:
                return;
            default:
                throw new IllegalArgumentException("Invalid status");
        }

        TicketNotification ticketNotification = new TicketNotification(this, oldStatus, newStatus, timestamp, username);
        notifyObservers(ticketNotification);
    }

}
