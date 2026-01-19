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
import reports.RiskReportMark;
import reports.TicketReportVisitor;
import reports.TicketRiskReport;
import utils.CmdCommonOutput;
import utils.MathUtils;
import utils.ReportTicketOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateTicketRiskReportCmd implements Command{
    private CommandInput cmdInput;

    public GenerateTicketRiskReportCmd(CommandInput cmdInput) {
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

        Map<TicketType, List<Double>> ticketsRiskScores = new HashMap<>();
        ticketsRiskScores.put(TicketType.BUG, new ArrayList<>());
        ticketsRiskScores.put(TicketType.FEATURE_REQUEST, new ArrayList<>());
        ticketsRiskScores.put(TicketType.UI_FEEDBACK, new ArrayList<>());

        TicketReportVisitor riskReport = new TicketRiskReport();

        for (Ticket ticket: database.getCreatedTickets()) {
            if (ticket.getStatus() == TicketStatus.OPEN ||  ticket.getStatus() == TicketStatus.IN_PROGRESS) {
                totalTickets++;

                int numTickets = ticketsTypeInventory.get(ticket.getType());
                ticketsTypeInventory.put(ticket.getType(), numTickets + 1);

                int numTicketsForPriority = ticketsByPriority.get(ticket.getBusinessPriority());
                ticketsByPriority.put(ticket.getBusinessPriority(), numTicketsForPriority + 1);

                double riskScore = ticket.accept(riskReport);
                ticketsRiskScores.get(ticket.getType()).add(riskScore);
            }
        }

        Map<TicketType, RiskReportMark> ticketsMarksByType = new HashMap<>();
        double bugScore = MathUtils.calculateAverageImpact(ticketsRiskScores.get(TicketType.BUG));
       ticketsMarksByType.put(TicketType.BUG, RiskReportMark.getMark(bugScore));

        double featureReqScore = MathUtils.calculateAverageImpact(ticketsRiskScores.get(TicketType.FEATURE_REQUEST));
       ticketsMarksByType.put(TicketType.FEATURE_REQUEST, RiskReportMark.getMark(featureReqScore));

        double uiFeedbackScore = MathUtils.calculateAverageImpact(ticketsRiskScores.get(TicketType.UI_FEEDBACK));
       ticketsMarksByType.put(TicketType.UI_FEEDBACK, RiskReportMark.getMark(uiFeedbackScore));

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        ObjectNode reportNode = ReportTicketOutput.toJson(totalTickets, ticketsTypeInventory,
                                                            ticketsByPriority, ticketsMarksByType);
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
