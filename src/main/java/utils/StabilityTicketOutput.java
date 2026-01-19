package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.tickets.TicketPriority;
import entities.tickets.TicketType;
import reports.AppStability;
import reports.RiskReportMark;

import java.util.Map;

public class StabilityTicketOutput {
    public static ObjectNode toJson(int openTickets,
                                    Map<TicketType, Integer> ticketsTypeInventory,
                                    Map<TicketPriority, Integer> ticketsByPriority,
                                    Map<TicketType, Double> ticketsImpactScore,
                                    Map<TicketType, RiskReportMark> ticketTypeMark,
                                    AppStability appStability) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode reportNode = objectMapper.createObjectNode();
        reportNode.put("totalOpenTickets", openTickets);

        ObjectNode ticketsByTypeNode = objectMapper.createObjectNode();
        for (TicketType ticketType : TicketType.values()) {
            ticketsByTypeNode.put(ticketType.toString(), ticketsTypeInventory.get(ticketType));
        }
        reportNode.set("openTicketsByType", ticketsByTypeNode);

        ObjectNode ticketsByPriorityNode = objectMapper.createObjectNode();
        for (TicketPriority ticketPriority : TicketPriority.values()) {
            ticketsByPriorityNode.put(ticketPriority.toString(), ticketsByPriority.get(ticketPriority));
        }
        reportNode.set("openTicketsByPriority", ticketsByPriorityNode);

        ObjectNode ticketsRiskNode = objectMapper.createObjectNode();
        ObjectNode ticketsImpactNode = objectMapper.createObjectNode();
        for (TicketType ticketType : TicketType.values()) {
            ticketsRiskNode.put(ticketType.toString(), ticketTypeMark.get(ticketType).toString());
            ticketsImpactNode.put(ticketType.toString(), ticketsImpactScore.get(ticketType));
        }

        reportNode.set("riskByType", ticketsRiskNode);
        reportNode.set("impactByType", ticketsImpactNode);
        reportNode.put("appStability", appStability.toString().replace("_", ""));

        return reportNode;
    }
}
