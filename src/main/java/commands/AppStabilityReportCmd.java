package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.tickets.Ticket;
import entities.tickets.TicketPriority;
import entities.tickets.TicketStatus;
import entities.tickets.TicketType;
import entities.users.UserRole;
import fileio.CommandInput;
import reports.*;
import utils.CmdCommonOutput;
import utils.MathUtils;
import utils.ReportTicketOutput;
import utils.StabilityTicketOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppStabilityReportCmd implements Command {
    private static final int CUSTOMER_IMPACT_LIMIT = 50;
    private CommandInput cmdInput;

    public AppStabilityReportCmd(CommandInput cmdInput) {
        this.cmdInput = cmdInput;

    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();

        Map<TicketType, Integer> openTicketsInventory = new HashMap<>();
        openTicketsInventory.put(TicketType.BUG, 0);
        openTicketsInventory.put(TicketType.FEATURE_REQUEST, 0);
        openTicketsInventory.put(TicketType.UI_FEEDBACK, 0);

        Map<TicketPriority, Integer> openTicketsByPriority = new HashMap<>();
        openTicketsByPriority.put(TicketPriority.LOW, 0);
        openTicketsByPriority.put(TicketPriority.MEDIUM, 0);
        openTicketsByPriority.put(TicketPriority.HIGH, 0);
        openTicketsByPriority.put(TicketPriority.CRITICAL, 0);

        Map<TicketType, List<Double>> ticketsImpactScores = new HashMap<>();
        ticketsImpactScores.put(TicketType.BUG, new ArrayList<>());
        ticketsImpactScores.put(TicketType.FEATURE_REQUEST, new ArrayList<>());
        ticketsImpactScores.put(TicketType.UI_FEEDBACK, new ArrayList<>());

        Map<TicketType, List<Double>> ticketsRiskScores = new HashMap<>();
        ticketsRiskScores.put(TicketType.BUG, new ArrayList<>());
        ticketsRiskScores.put(TicketType.FEATURE_REQUEST, new ArrayList<>());
        ticketsRiskScores.put(TicketType.UI_FEEDBACK, new ArrayList<>());

        TicketReportVisitor customerImpact = new CustomerImpactReport();
        TicketReportVisitor riskReport = new TicketRiskReport();

        int openTickets = 0;
        int ticketsInProgress = 0;

        for (Ticket ticket: database.getCreatedTickets()) {
            if (ticket.getStatus() == TicketStatus.OPEN) {
                openTickets++;

                int numTickets = openTicketsInventory.get(ticket.getType());
                openTicketsInventory.put(ticket.getType(), numTickets + 1);

                int numTicketsForPriority = openTicketsByPriority.get(ticket.getBusinessPriority());
                openTicketsByPriority.put(ticket.getBusinessPriority(), numTicketsForPriority + 1);

                double impactScore = ticket.accept(customerImpact);
                ticketsImpactScores.get(ticket.getType()).add(impactScore);

                double riskScore = ticket.accept(riskReport);
                ticketsRiskScores.get(ticket.getType()).add(riskScore);
            }

            if (ticket.getStatus() == TicketStatus.IN_PROGRESS) {
                ticketsInProgress++;
            }
        }

        double bugScore;
        double featureReqScore;
        double uiFeedbackScore;

        Map<TicketType, Double> ticketTypeAverageScore = new HashMap<>();
        bugScore = MathUtils.calculateAverageImpact(ticketsImpactScores.get(TicketType.BUG));
        ticketTypeAverageScore.put(TicketType.BUG, MathUtils.roundResult(bugScore));

        featureReqScore = MathUtils.calculateAverageImpact(ticketsImpactScores.get(TicketType.FEATURE_REQUEST));
        ticketTypeAverageScore.put(TicketType.FEATURE_REQUEST, MathUtils.roundResult(featureReqScore));

        uiFeedbackScore = MathUtils.calculateAverageImpact(ticketsImpactScores.get(TicketType.UI_FEEDBACK));
        ticketTypeAverageScore.put(TicketType.UI_FEEDBACK, MathUtils.roundResult(uiFeedbackScore));

        Map<TicketType, RiskReportMark> ticketTypeMark = new HashMap<>();
        bugScore = MathUtils.calculateAverageImpact(ticketsRiskScores.get(TicketType.BUG));
        ticketTypeMark.put(TicketType.BUG, RiskReportMark.getMark(bugScore));

        featureReqScore = MathUtils.calculateAverageImpact(ticketsRiskScores.get(TicketType.FEATURE_REQUEST));
        ticketTypeMark.put(TicketType.FEATURE_REQUEST, RiskReportMark.getMark(featureReqScore));

        uiFeedbackScore = MathUtils.calculateAverageImpact(ticketsRiskScores.get(TicketType.UI_FEEDBACK));
        ticketTypeMark.put(TicketType.UI_FEEDBACK, RiskReportMark.getMark(uiFeedbackScore));

        AppStability appStability = calculateAppStability(openTickets, ticketsInProgress, ticketTypeAverageScore, ticketTypeMark);
        if (appStability == AppStability.STABLE) {
            database.setAppStopped(true);
        }

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        ObjectNode reportNode = StabilityTicketOutput.toJson(openTickets,
                                                            openTicketsInventory,
                                                            openTicketsByPriority,
                                                            ticketTypeAverageScore,
                                                            ticketTypeMark,
                                                            appStability);
        objNode.set("report", reportNode);
        return objNode;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.MANAGER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }

    public AppStability calculateAppStability(int openTickets,
                                              int ticketsInProgress,
                                              Map<TicketType, Double> ticketTypeAverageScore,
                                              Map<TicketType, RiskReportMark> ticketTypeMark) {
        boolean impactUnderFifty = true;
        boolean negligibleRisk = true;

        if (openTickets == 0 && ticketsInProgress == 0) {
            return AppStability.STABLE;
        }

        for (Map.Entry<TicketType, Double> entry: ticketTypeAverageScore.entrySet()) {
            if (entry.getValue() >= CUSTOMER_IMPACT_LIMIT) {
                impactUnderFifty = false;
            }
        }

        for (Map.Entry<TicketType, RiskReportMark> entry: ticketTypeMark.entrySet()) {
            if (entry.getValue() != RiskReportMark.NEGLIGIBLE) {
                negligibleRisk = false;
            }

            if (entry.getValue() == RiskReportMark.SIGNIFICANT) {
                return AppStability.UNSTABLE;
            }
        }

        if (impactUnderFifty && negligibleRisk) {
            return AppStability.STABLE;
        }

        return AppStability.PARTIALLY_STABLE;
    }
}
