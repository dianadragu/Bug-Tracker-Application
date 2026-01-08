package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.tickets.Ticket;
import entities.tickets.TicketStatus;
import entities.users.Developer;
import entities.users.UserRole;
import fileio.CommandInput;
import utils.CmdCommonOutput;

import java.util.ArrayList;
import java.util.List;

public class UndoAssignTicketCmd implements Command {
    private CommandInput cmdInput;

    public UndoAssignTicketCmd(CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();
        int assignedTicketId = cmdInput.getTicketID();
        Ticket ticket = database.getTicketById(assignedTicketId);

        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
            objNode.put("error", "Only IN_PROGRESS tickets can be unassigned.");
            return objNode;
        }

        Developer dev = (Developer) database.findUser(cmdInput.getUsername());
        dev.getAssignedTickets().remove(ticket);
        dev.removeAssignedTicketFromMilestone(assignedTicketId);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAssignedAt(null);
        return null;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.DEVELOPER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
