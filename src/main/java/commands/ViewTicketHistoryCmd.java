package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.milestones.Milestone;
import entities.tickets.Ticket;

import entities.users.Developer;

import entities.users.User;
import entities.users.UserRole;
import fileio.CommandInput;
import utils.CmdCommonOutput;
import utils.StandardTicketHistoryOutput;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewTicketHistoryCmd implements Command {
    private CommandInput cmdInput;

    public ViewTicketHistoryCmd(final CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();
        User user = database.findUser(cmdInput.getUsername());

        List<Ticket> tickets = new ArrayList<Ticket>();

        switch (user.getRole()) {
            case MANAGER:
                for (Milestone milestone : user.getMilestones()) {
                    tickets.addAll(milestone.getAssignedTickets());
                }
                break;
            case DEVELOPER:
                Developer developer = (Developer) user;
                tickets.addAll(developer.getAssignedTickets());
                tickets.addAll(developer.getLostTickets());
                break;
            default:
                throw new IllegalArgumentException();
        }

        tickets.sort(new Comparator<Ticket>() {
            @Override
            public int compare(final Ticket o1, final Ticket o2) {
                int result = o1.getCreatedAt().compareTo(o2.getCreatedAt());
                if (result == 0) {
                    return o1.getId() - o2.getId();
                } else {
                    return result;
                }
            }
        });

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        for (Ticket ticket : tickets) {
            arrayNode.add(StandardTicketHistoryOutput.toJson(ticket));
        }

        objNode.set("ticketHistory", arrayNode);
        return objNode;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.DEVELOPER);
        acceptedRoles.add(UserRole.MANAGER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
