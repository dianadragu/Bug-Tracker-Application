package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.tickets.Ticket;

import java.util.List;

public class FilterTicketOutput {
    public static ObjectNode toJson(Ticket ticket, List<String> words) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objNode = objectMapper.createObjectNode();

        objNode.put("id", ticket.getId());
        objNode.put("type", ticket.getType().toString());
        objNode.put("title", convertNullToEmptyString(ticket.getTitle()));
        objNode.put("businessPriority", ticket.getBusinessPriority().toString());
        objNode.put("status", ticket.getStatus().toString());
        objNode.put("createdAt", ticket.getCreatedAt());
        objNode.put("solvedAt", convertNullToEmptyString(ticket.getSolvedAt()));
        objNode.put("reportedBy", convertNullToEmptyString(ticket.getReportedBy()));

        if (words != null) {
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (String word : words) {
                arrayNode.add(word);
            }
            objNode.set("matchingWords", arrayNode);
        }

        return objNode;
    }

    private static String convertNullToEmptyString(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }


}
