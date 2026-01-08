package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.tickets.Comment;
import entities.tickets.Ticket;
import entities.tickets.TicketStatus;
import entities.users.Developer;
import entities.users.Reporter;
import entities.users.User;
import entities.users.UserRole;
import fileio.CommandInput;
import utils.CmdCommonOutput;

import java.util.ArrayList;
import java.util.List;

public class AddCommentCmd implements Command {
    private final static int MINIMUM_COMMENT_SIZE = 10;
    private CommandInput cmdInput;

    public AddCommentCmd(CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        AppDatabase database = AppDatabase.getInstance();
        Ticket ticket = database.getTicketById(cmdInput.getTicketID());

        if (ticket == null) {
            return null;
        }

        if (ticket.getReportedBy().isEmpty()) {
            objNode.put("error", "Comments are not allowed on anonymous tickets.");
            return objNode;
        }

        User user = database.findUser(cmdInput.getUsername());

        if (user.getRole() == UserRole.REPORTER && ticket.getStatus() == TicketStatus.CLOSED) {
            objNode.put("error", "Reporters cannot comment on CLOSED tickets.");
            return objNode;
        }

        if (cmdInput.getComment().length() < MINIMUM_COMMENT_SIZE) {
            objNode.put("error", "Comment must be at least 10 characters long.");
            return objNode;
        }

        if (user.getRole() == UserRole.DEVELOPER) {
            Developer dev = (Developer) user;
            if (!dev.getAssignedTickets().contains(ticket)) {
                objNode.put("error", "Ticket " + ticket.getId() +
                            " is not assigned to the developer " + dev.getUsername() + ".");
                return objNode;
            }
        }

        if (user.getRole() == UserRole.REPORTER) {
            Reporter reporter = (Reporter) user;
            if (!reporter.getReportedTickets().contains(ticket)) {
                objNode.put("error", "Reporter " + reporter.getUsername() +
                        " cannot comment on ticket " + ticket.getId() + ".");
                return objNode;
            }
        }

        Comment comment = new Comment(cmdInput.getUsername(), cmdInput.getComment(), cmdInput.getTimestamp());
        ticket.getComments().add(comment);
        return null;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.DEVELOPER);
        acceptedRoles.add(UserRole.REPORTER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
