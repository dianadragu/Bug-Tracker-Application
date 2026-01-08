package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.tickets.Comment;
import entities.tickets.Ticket;
import entities.users.UserRole;
import fileio.CommandInput;
import utils.CmdCommonOutput;

import java.util.ArrayList;
import java.util.List;

public class UndoAddCommentCmd implements Command{
    private CommandInput cmdInput;

    public UndoAddCommentCmd(CommandInput cmdInput) {
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

        if (ticket == null ) {
            return null;
        }

        if (ticket.getReportedBy().isEmpty()) {
            objNode.put("error", "Comments are not allowed on anonymous tickets.");
            return objNode;
        }

        if (ticket.getLastComment(cmdInput.getUsername()) == null) {
            return null;
        }

        Comment lastComm = ticket.getLastComment(cmdInput.getUsername());
        ticket.getComments().remove(lastComm);
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
