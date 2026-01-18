package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.milestones.Milestone;
import entities.tickets.Ticket;
import entities.tickets.TicketStatus;
import entities.users.Developer;
import entities.users.Reporter;
import entities.users.User;
import entities.users.UserRole;
import fileio.CommandInput;
import utils.CmdCommonOutput;
import utils.StandardTicketOutput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewNotificationsCmd implements Command {
    private CommandInput cmdInput;

    public ViewNotificationsCmd(CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();
        Developer dev = (Developer) database.findUser(cmdInput.getUsername());

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        for (String notification : dev.getNotifications()) {
            arrayNode.add(notification);
        }

        dev.getNotifications().clear();
        objNode.set("notifications", arrayNode);
        return objNode;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.DEVELOPER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
