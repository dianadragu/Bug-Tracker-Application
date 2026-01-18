package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.tickets.Ticket;
import entities.tickets.Comment;
import entities.tickets.TicketHistory;

public class StandardTicketOutput {
    public static ObjectNode toJson(Ticket ticket) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objNode = objectMapper.createObjectNode();

        objNode.put("id", ticket.getId());
        objNode.put("type", ticket.getType().toString());
        objNode.put("title", convertNullToEmptyString(ticket.getTitle()));
        objNode.put("businessPriority", ticket.getBusinessPriority().toString());
        objNode.put("status", ticket.getStatus().toString());
        objNode.put("createdAt", ticket.getCreatedAt());
        objNode.put("solvedAt", convertNullToEmptyString(ticket.getSolvedAt()));
        objNode.put("assignedAt", convertNullToEmptyString(ticket.getAssignedAt()));
        objNode.put("assignedTo", convertNullToEmptyString(ticket.getAssignedTo()));
        objNode.put("reportedBy", convertNullToEmptyString(ticket.getReportedBy()));

        ArrayNode commArrayNode = objectMapper.createArrayNode();

        if(ticket.getComments() != null) {
            for (Comment comment : ticket.getComments()) {
                ObjectNode commentNode = objectMapper.createObjectNode();
                commentNode.put("author", comment.getAuthor());
                commentNode.put("content", comment.getContent());
                commentNode.put("createdAt", comment.getCreatedAt());
                commArrayNode.add(commentNode);
            }
        }
        objNode.set("comments", commArrayNode);
        return objNode;
    }

    private static String convertNullToEmptyString(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }
}
