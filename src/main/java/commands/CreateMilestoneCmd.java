package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.milestones.Milestone;
import entities.milestones.observers.*;
import entities.tickets.Ticket;
import entities.tickets.TicketHistory;
import entities.users.Developer;
import entities.users.Manager;
import entities.users.User;
import entities.users.UserRole;
import fileio.CommandInput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.CmdCommonOutput;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateMilestoneCmd implements Command {
    private CommandInput cmdInput;
    private AppDatabase database;
    public CreateMilestoneCmd(CommandInput cmdInput) {
        this.cmdInput = cmdInput;
        this.database = AppDatabase.getInstance();
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        List<Integer> tickets = cmdInput.getTickets();
        for (Integer ticketId : tickets) {
            String otherMilestone = database.assignedToOtherMilestone(ticketId);
            if(otherMilestone != null) {
                ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
                objNode.put("error", "Tickets " + ticketId + " already assigned to milestone " + otherMilestone + ".");
                return objNode;
            }
        }
        // logica pt manageri
        Milestone milestone = new Milestone(cmdInput.getName(), cmdInput.getDueDate(), cmdInput.getBlockingFor(), cmdInput.getTickets(), cmdInput.getAssignedDevs());
        milestone.setCreatedAt(LocalDate.parse(cmdInput.getTimestamp()));
        milestone.setUpdatedAt(milestone.getCreatedAt());
        milestone.setCreatedBy(cmdInput.getUsername());
        milestone.blockMilestones();

        User manager = database.findUser(cmdInput.getUsername());
        manager.getMilestones().add(milestone);
        database.getCreatedMilestones().add(milestone);

        // logica pt developeri
        for (String devName : cmdInput.getAssignedDevs()) {
            User dev = database.findUser(devName);
            dev.getMilestones().add(milestone);
        }

        for (Integer ticketId : cmdInput.getTickets()) {
            Ticket ticket = database.getTicketById(ticketId);
            TicketHistory ticketHistory = new TicketHistory();
            ticket.getTicketHistory().add(ticketHistory.saveMilestoneAddition(cmdInput.getUsername(),
                                                                                cmdInput.getTimestamp(),
                                                                                cmdInput.getName()));
        }
        milestone.addObserver(new MilestoneCompleted());
        milestone.addObserver(new MilestoneCreated());
        milestone.addObserver(new MilestoneDueDateIsTomorrow());
        milestone.addObserver(new MilestonePassedDueDate());

        milestone.sendNotificationForCreatedMilestone(LocalDate.parse(cmdInput.getTimestamp()));
        return null;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.MANAGER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
