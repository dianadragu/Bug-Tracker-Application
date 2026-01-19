package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.tickets.Ticket;
import entities.tickets.TicketPriority;
import entities.tickets.TicketType;
import reports.RiskReportMark;

import java.util.List;
import java.util.Map;


public class ReportTicketOutput {
    public static ObjectNode toJson(int totalTickets,
                                    Map<TicketType, Integer> ticketsTypeInventory,
                                    Map<TicketPriority, Integer> ticketsByPriority,
                                    Map<TicketType, Double> ticketsMetric,
                                    String reportName) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode reportNode = objectMapper.createObjectNode();
        reportNode.put("totalTickets", totalTickets);

        ObjectNode ticketsByTypeNode = objectMapper.createObjectNode();
        for (TicketType ticketType : TicketType.values()) {
            ticketsByTypeNode.put(ticketType.toString(), ticketsTypeInventory.get(ticketType));
        }
        reportNode.set("ticketsByType", ticketsByTypeNode);

        ObjectNode ticketsByPriorityNode = objectMapper.createObjectNode();
        for (TicketPriority ticketPriority : TicketPriority.values()) {
            ticketsByPriorityNode.put(ticketPriority.toString(), ticketsByPriority.get(ticketPriority));
        }
        reportNode.set("ticketsByPriority", ticketsByPriorityNode);

        ObjectNode ticketsMetricNode = objectMapper.createObjectNode();
        for (TicketType ticketType : TicketType.values()) {
            ticketsMetricNode.put(ticketType.toString(), ticketsMetric.get(ticketType));
        }
        reportNode.set(reportName, ticketsMetricNode);

        return reportNode;
    }

    public static ObjectNode toJson(int totalTickets,
                                        Map<TicketType, Integer> ticketsTypeInventory,
                                        Map<TicketPriority, Integer> ticketsByPriority,
                                        Map<TicketType, RiskReportMark> ticketsMetric) {

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode reportNode = objectMapper.createObjectNode();
        reportNode.put("totalTickets", totalTickets);

        ObjectNode ticketsByTypeNode = objectMapper.createObjectNode();
        for (TicketType ticketType : TicketType.values()) {
            ticketsByTypeNode.put(ticketType.toString(), ticketsTypeInventory.get(ticketType));
        }
        reportNode.set("ticketsByType", ticketsByTypeNode);

        ObjectNode ticketsByPriorityNode = objectMapper.createObjectNode();
        for (TicketPriority ticketPriority : TicketPriority.values()) {
            ticketsByPriorityNode.put(ticketPriority.toString(), ticketsByPriority.get(ticketPriority));
        }
        reportNode.set("ticketsByPriority", ticketsByPriorityNode);

        ObjectNode ticketsMetricNode = objectMapper.createObjectNode();
        for (TicketType ticketType : TicketType.values()) {
            ticketsMetricNode.put(ticketType.toString(), ticketsMetric.get(ticketType).toString());
        }
        reportNode.set("riskByType", ticketsMetricNode);

        return reportNode;
    }


}
