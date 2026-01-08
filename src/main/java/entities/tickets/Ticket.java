package entities.tickets;

import com.fasterxml.jackson.annotation.JsonInclude;
import entities.users.DevSeniority;
import entities.users.ExpertiseArea;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public abstract class Ticket {
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
}
