package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.tickets.Comment;
import entities.tickets.Ticket;

public class StandardAssignedTicketOutput {
    public static ObjectNode toJson(Ticket ticket) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objNode = objectMapper.createObjectNode();

        objNode.put("id", ticket.getId());
        objNode.put("type", ticket.getType().toString());
        objNode.put("title", convertNullToEmptyString(ticket.getTitle()));
        objNode.put("businessPriority", ticket.getBusinessPriority().toString());
        objNode.put("status", ticket.getStatus().toString());
        objNode.put("createdAt", ticket.getCreatedAt());
        objNode.put("assignedAt", ticket.getAssignedAt());
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
