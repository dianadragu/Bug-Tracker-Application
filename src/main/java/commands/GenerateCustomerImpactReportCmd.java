package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.tickets.Ticket;
import entities.tickets.TicketPriority;
import entities.tickets.TicketStatus;
import entities.tickets.TicketType;
import entities.users.UserRole;
import fileio.CommandInput;
import reports.CustomerImpactReport;
import reports.TicketReportVisitor;
import utils.CmdCommonOutput;
import utils.MathUtils;
import utils.ReportTicketOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateCustomerImpactReportCmd implements Command {
    private CommandInput cmdInput;

    public GenerateCustomerImpactReportCmd(CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();
        int totalTickets = 0;

        Map<TicketType, Integer> ticketsTypeInventory = new HashMap<>();
        ticketsTypeInventory.put(TicketType.BUG, 0);
        ticketsTypeInventory.put(TicketType.FEATURE_REQUEST, 0);
        ticketsTypeInventory.put(TicketType.UI_FEEDBACK, 0);

        Map<TicketPriority, Integer> ticketsByPriority = new HashMap<>();
        ticketsByPriority.put(TicketPriority.LOW, 0);
        ticketsByPriority.put(TicketPriority.MEDIUM, 0);
        ticketsByPriority.put(TicketPriority.HIGH, 0);
        ticketsByPriority.put(TicketPriority.CRITICAL, 0);

        Map<TicketType, List<Double>> ticketsImpactScores = new HashMap<>();
        ticketsImpactScores.put(TicketType.BUG, new ArrayList<>());
        ticketsImpactScores.put(TicketType.FEATURE_REQUEST, new ArrayList<>());
        ticketsImpactScores.put(TicketType.UI_FEEDBACK, new ArrayList<>());

        TicketReportVisitor customerImpact = new CustomerImpactReport();

        for (Ticket ticket: database.getCreatedTickets()) {
            if (ticket.getStatus() == TicketStatus.OPEN ||  ticket.getStatus() == TicketStatus.IN_PROGRESS) {
                totalTickets++;

                int numTickets = ticketsTypeInventory.get(ticket.getType());
                ticketsTypeInventory.put(ticket.getType(), numTickets + 1);

                int numTicketsForPriority = ticketsByPriority.get(ticket.getBusinessPriority());
                ticketsByPriority.put(ticket.getBusinessPriority(), numTicketsForPriority + 1);

                double impactScore = ticket.accept(customerImpact);
                ticketsImpactScores.get(ticket.getType()).add(impactScore);
            }
        }

        Map<TicketType, Double> ticketTypeAverageScore = new HashMap<>();
        double bugScore = MathUtils.calculateAverageImpact(ticketsImpactScores.get(TicketType.BUG));
        ticketTypeAverageScore.put(TicketType.BUG, MathUtils.roundResult(bugScore));

        double featureReqScore = MathUtils.calculateAverageImpact(ticketsImpactScores.get(TicketType.FEATURE_REQUEST));
        ticketTypeAverageScore.put(TicketType.FEATURE_REQUEST, MathUtils.roundResult(featureReqScore));

        double uiFeedbackScore = MathUtils.calculateAverageImpact(ticketsImpactScores.get(TicketType.UI_FEEDBACK));
        ticketTypeAverageScore.put(TicketType.UI_FEEDBACK, MathUtils.roundResult(uiFeedbackScore));

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        ObjectNode reportNode = ReportTicketOutput.toJson(totalTickets, ticketsTypeInventory,
                                                        ticketsByPriority, ticketTypeAverageScore,
                                                        "customerImpactByType");
        objNode.set("report", reportNode);
        return objNode;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.MANAGER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
