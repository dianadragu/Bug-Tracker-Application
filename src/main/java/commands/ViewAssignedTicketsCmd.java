package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.tickets.Ticket;
import entities.users.Developer;
import entities.users.UserRole;
import fileio.CommandInput;
import utils.CmdCommonOutput;
import utils.StandardAssignedTicketOutput;
import utils.StandardTicketOutput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewAssignedTicketsCmd implements Command {
    private CommandInput cmdInput;

    public ViewAssignedTicketsCmd(final CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        AppDatabase database = AppDatabase.getInstance();
        Developer dev = (Developer) database.findUser(cmdInput.getUsername());

        List<Ticket> assignedTickets = dev.getAssignedTickets();

        assignedTickets.sort(new Comparator<Ticket>() {
            // compareTo poate compara enumuri dupa nr lor de ordine, nu dupa valoare
            @Override
            public int compare(Ticket o1, Ticket o2) {
                if (o2.getBusinessPriority().compareTo(o1.getBusinessPriority()) == 0) {
                    if (o1.getCreatedAt().compareTo(o2.getCreatedAt()) == 0) {
                        return o1.getId() - o2.getId();
                    }
                    return o1.getCreatedAt().compareTo(o2.getCreatedAt());
                }
                return o2.getBusinessPriority().compareTo(o1.getBusinessPriority());
            }
        });

        for (Ticket ticket : assignedTickets) {
            arrayNode.add(StandardAssignedTicketOutput.toJson(ticket));
        }

        objNode.set("assignedTickets", arrayNode);

        return objNode;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.DEVELOPER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
