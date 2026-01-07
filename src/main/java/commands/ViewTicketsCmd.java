package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.tickets.Ticket;
import entities.users.User;
import entities.users.UserRole;
import fileio.CommandInput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.CmdCommonOutput;
import utils.StandardTicketOutput;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ViewTicketsCmd implements Command {
    private CommandInput cmdInput;

    public ViewTicketsCmd(final CommandInput cmdInput) {
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
               tickets.addAll(database.getCreatedTickets());
               break;
           case REPORTER:
               List<Ticket> reportedTickets = database.getReportedTickets()
                                                .get((cmdInput.getUsername()));
               if (reportedTickets != null) {
                   tickets.addAll(reportedTickets);
               }
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
            arrayNode.add(StandardTicketOutput.toJson(ticket));
        }

        objNode.set("tickets", arrayNode);
        return objNode;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.MANAGER);
        acceptedRoles.add(UserRole.REPORTER);
        acceptedRoles.add(UserRole.DEVELOPER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
