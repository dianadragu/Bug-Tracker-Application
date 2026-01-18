package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.tickets.Comment;
import entities.tickets.Ticket;
import entities.tickets.TicketHistory;

public class StandardTicketHistoryOutput {
    public static ObjectNode toJson(Ticket ticket) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objNode = objectMapper.createObjectNode();

        objNode.put("id", ticket.getId());
        objNode.put("title", convertNullToEmptyString(ticket.getTitle()));
        objNode.put("status", ticket.getStatus().toString());

        ArrayNode actionsArrayNode = objectMapper.createArrayNode();

        if(ticket.getTicketHistory() != null) {
            for (TicketHistory history : ticket.getTicketHistory()) {
                ObjectNode historyNode = objectMapper.createObjectNode();
                if (history.getMilestone() != null) {
                    historyNode.put("milestone", history.getMilestone());
                }
                if (history.getFrom() != null) {
                    historyNode.put("from", history.getFrom());
                }
                if (history.getTo() != null) {
                    historyNode.put("to", history.getTo());
                }
                historyNode.put("by", history.getBy());
                historyNode.put("timestamp", history.getTimestamp());
                historyNode.put("action", history.getAction());
                actionsArrayNode.add(historyNode);
            }
        }
        objNode.set("actions", actionsArrayNode);

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
