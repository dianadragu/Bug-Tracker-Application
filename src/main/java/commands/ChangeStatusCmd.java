package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.tickets.Ticket;
import entities.tickets.TicketHistory;
import entities.tickets.TicketStatus;
import entities.users.Developer;
import entities.users.UserRole;
import fileio.CommandInput;
import utils.CmdCommonOutput;

import java.util.ArrayList;
import java.util.List;

public class ChangeStatusCmd implements Command {
    private CommandInput cmdInput;

    public ChangeStatusCmd(final CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    };

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();
        Developer dev = (Developer) database.findUser(cmdInput.getUsername());
        Ticket ticket = database.getTicketById(cmdInput.getTicketID());

        if (!dev.getAssignedTickets().contains(ticket)) {
            ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
            objNode.put("error", "Ticket " + cmdInput.getTicketID() +
                    " is not assigned to developer " + dev.getUsername() + ".");
            return objNode;
        }

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            return null;
        }

        ticket.switchStatus(cmdInput.getTimestamp(), cmdInput.getUsername());
        return null;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.DEVELOPER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
