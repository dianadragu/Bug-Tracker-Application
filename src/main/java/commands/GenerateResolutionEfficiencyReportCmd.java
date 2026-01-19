package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.tickets.Ticket;
import entities.tickets.TicketPriority;
import entities.tickets.TicketStatus;
import entities.tickets.TicketType;
import entities.users.UserRole;
import fileio.CommandInput;

import reports.ResolutionEfficiencyReport;
import reports.TicketReportVisitor;
import utils.CmdCommonOutput;
import utils.MathUtils;
import utils.ReportTicketOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateResolutionEfficiencyReportCmd implements Command{
    private CommandInput cmdInput;

    public GenerateResolutionEfficiencyReportCmd(CommandInput cmdInput) {
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

        Map<TicketType, List<Double>> ticketsEfficiencyScores = new HashMap<>();
        ticketsEfficiencyScores.put(TicketType.BUG, new ArrayList<>());
        ticketsEfficiencyScores.put(TicketType.FEATURE_REQUEST, new ArrayList<>());
        ticketsEfficiencyScores.put(TicketType.UI_FEEDBACK, new ArrayList<>());

        TicketReportVisitor efficiencyReport = new ResolutionEfficiencyReport();

        for (Ticket ticket: database.getCreatedTickets()) {
            if (ticket.getStatus() == TicketStatus.RESOLVED ||  ticket.getStatus() == TicketStatus.CLOSED) {
                totalTickets++;

                int numTickets = ticketsTypeInventory.get(ticket.getType());
                ticketsTypeInventory.put(ticket.getType(), numTickets + 1);

                int numTicketsForPriority = ticketsByPriority.get(ticket.getBusinessPriority());
                ticketsByPriority.put(ticket.getBusinessPriority(), numTicketsForPriority + 1);

                double impactScore = ticket.accept(efficiencyReport);
                ticketsEfficiencyScores.get(ticket.getType()).add(impactScore);
            }
        }

        Map<TicketType, Double> ticketTypeAverageScore = new HashMap<>();
        double bugScore = MathUtils.calculateAverageImpact(ticketsEfficiencyScores.get(TicketType.BUG));
        ticketTypeAverageScore.put(TicketType.BUG, MathUtils.roundResult(bugScore));

        double featureReqScore = MathUtils.calculateAverageImpact(ticketsEfficiencyScores.get(TicketType.FEATURE_REQUEST));
        ticketTypeAverageScore.put(TicketType.FEATURE_REQUEST, MathUtils.roundResult(featureReqScore));

        double uiFeedbackScore = MathUtils.calculateAverageImpact(ticketsEfficiencyScores.get(TicketType.UI_FEEDBACK));
        ticketTypeAverageScore.put(TicketType.UI_FEEDBACK, MathUtils.roundResult(uiFeedbackScore));

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        ObjectNode reportNode = ReportTicketOutput.toJson(totalTickets, ticketsTypeInventory,
                                                        ticketsByPriority, ticketTypeAverageScore,
                                                        "efficiencyByType");
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
