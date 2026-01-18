package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.milestones.Milestone;
import entities.tickets.Ticket;
import entities.tickets.TicketHistory;
import entities.tickets.TicketStatus;
import entities.users.*;
import fileio.CommandInput;
import utils.CmdCommonOutput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AssignTicketCmd implements Command {
    private CommandInput cmdInput;

    public AssignTicketCmd(final CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();
        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        Developer dev = (Developer) database.findUser(cmdInput.getUsername());
        int assignedTicketId = cmdInput.getTicketID();
        Ticket ticket = database.getTicketById(assignedTicketId);
        List<ExpertiseArea> expertiseAreas = ticket.getExpertiseAreas();
        List<DevSeniority> requiredSeniority = ticket.getRequiredSeniority();

        if (!expertiseAreas.contains(dev.getExpertiseArea())) {
            expertiseAreas.sort(new Comparator<ExpertiseArea>() {
                @Override
                public int compare(ExpertiseArea o1, ExpertiseArea o2) {
                    return o1.compareTo(o2);
                }
            });
            objNode.put("error", "Developer " + dev.getUsername() +
                    " cannot assign ticket " + assignedTicketId + " due to expertise area. Required: " +
                    ticket.getExpertiseAreas().toString().replace("[", "").replace("]", "") +
                    "; Current: " + dev.getExpertiseArea() + ".");
            return objNode;
        }

        if (!requiredSeniority.contains(dev.getSeniority())) {
            requiredSeniority.sort(new Comparator<DevSeniority>() {
                @Override
                public int compare(DevSeniority o1, DevSeniority o2) {
                    return o1.compareTo(o2);
                }
            });
            objNode.put("error", "Developer " + dev.getUsername() +
                    " cannot assign ticket " + assignedTicketId + " due to seniority level. Required: " +
                    requiredSeniority.toString().replace("[", "").replace("]", "") +
                    "; Current: " + dev.getSeniority() + ".");
            return objNode;
        }

        if (ticket.getStatus() != TicketStatus.OPEN) {
            objNode.put("error", "Only OPEN tickets can be assigned.");
            return objNode;
        }

        // verific milestone-urile
        for (Milestone milestone: database.getCreatedMilestones()) {
            if (milestone.getTickets().contains(assignedTicketId) &&
                    !milestone.getAssignedDevs().contains(dev.getUsername())) {
                objNode.put("error", "Developer " + dev.getUsername() +
                        " is not assigned to milestone " + milestone.getName() + ".");
                return objNode;
            }
        }

        for (Milestone milestone: dev.getMilestones()) {
            if (milestone.getTickets().contains(assignedTicketId) && milestone.isBlocked()) {
                objNode.put("error", "Cannot assign ticket " + assignedTicketId +
                        " from blocked milestone " + milestone.getName() + ".");
                return objNode;
            }
        }

        dev.updateAssignedTicketInMilestone(assignedTicketId);
        dev.getAssignedTickets().add(ticket);
        TicketHistory assignHistory = new TicketHistory();
        ticket.getTicketHistory().add(assignHistory.saveAssignment(cmdInput.getUsername(), cmdInput.getTimestamp()));

        ticket.switchStatus(cmdInput.getTimestamp(), cmdInput.getUsername());
        ticket.setAssignedAt(cmdInput.getTimestamp());
        return null;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole > acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.DEVELOPER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
