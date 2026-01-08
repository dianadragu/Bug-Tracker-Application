package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import database.WorkflowPhase;
import entities.tickets.TicketLoaderVisitor;
import entities.tickets.Ticket;
import entities.users.Reporter;
import entities.users.UserRole;
import fileio.CommandInput;
import fileio.ReportedTicketParamsInput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.CmdCommonOutput;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReportTicketCmd implements Command {
    private CommandInput cmdInput;
    private ReportedTicketParamsInput ticketParams;

    public ReportTicketCmd(final CommandInput cmdInput) {
        this.cmdInput = cmdInput;
        this.ticketParams = cmdInput.getParams();
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();
        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);

        if (database.getWorkflowPhase() != WorkflowPhase.TESTING) {
            objNode.put("error", "Tickets can only be reported during testing phases.");
            return objNode;
        } else if (ticketParams.getReportedBy().isEmpty() &&
                !ticketParams.getType().equals("BUG")) {
            objNode.put("error", "Anonymous reports are only allowed for tickets of type BUG.");
            return objNode;
        } else {
            TicketLoaderVisitor visitor = new TicketLoaderVisitor();
            Ticket newTicket = ticketParams.accept(visitor);
            newTicket.setCreatedAt(cmdInput.getTimestamp());

            int ticketId = database.getAvailableTicketId();
            newTicket.setId(ticketId);
            database.setAvailableTicketId(++ticketId);

            database.getCreatedTickets().add(newTicket);
            Reporter reporter = (Reporter) database.findUser(cmdInput.getUsername());
            reporter.getReportedTickets().add(newTicket);
            return null;
        }
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.REPORTER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
