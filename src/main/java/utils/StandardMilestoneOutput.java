package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.milestones.Milestone;
import entities.tickets.Ticket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StandardMilestoneOutput {
    public static ObjectNode toJson(Milestone milestone) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objNode = objectMapper.createObjectNode();

        objNode.put("name", milestone.getName());

        ArrayNode blckForArray = objectMapper.createArrayNode();
        for (String blck: milestone.getBlockingFor())
            blckForArray.add(blck);
        objNode.set("blockingFor", blckForArray);

        objNode.put("dueDate", milestone.getDueDate().toString());
        objNode.put("createdAt", milestone.getCreatedAt().toString());

        ArrayNode ticketsArray = objectMapper.createArrayNode();
        for (Integer id: milestone.getTickets())
            ticketsArray.add(id);
        objNode.set("tickets", ticketsArray);

        ArrayNode devArray = objectMapper.createArrayNode();
        for (String dev: milestone.getAssignedDevs())
            devArray.add(dev);
        objNode.set("assignedDevs", devArray);

        objNode.put("createdBy", milestone.getCreatedBy());
        objNode.put("status", milestone.getStatus().toString());
        objNode.put("isBlocked", milestone.isBlocked());
        objNode.put("daysUntilDue", milestone.getDaysUntilDue());
        objNode.put("overdueBy", milestone.getOverdueBy());

        ArrayNode openTicketsArray = objectMapper.createArrayNode();
        for (Integer id: milestone.getOpenTickets())
            openTicketsArray.add(id);
        objNode.set("openTickets", openTicketsArray);

        ArrayNode closedTicketsArray = objectMapper.createArrayNode();
        for (Integer id: milestone.getClosedTickets())
            closedTicketsArray.add(id);
        objNode.set("closedTickets", closedTicketsArray);

        objNode.put("completionPercentage", MathUtils.roundResult(milestone.calculateCompletionPercentage()));


        // sortez Mapa
        List<Map.Entry<String, List<Integer>>> entriesList = new ArrayList<>(milestone.getRepartition().entrySet());

        entriesList.sort(new Comparator<Map.Entry<String, List<Integer>>>() {
                             @Override
                             public int compare(final Map.Entry<String, List<Integer>> o1,
                                                final Map.Entry<String, List<Integer>> o2) {
                                 int result = o1.getValue().size() - o2.getValue().size();
                                 if (result == 0) {
                                     return o1.getKey().compareTo(o2.getKey());
                                 }
                                 return result;
                             }
                         });

        ArrayNode repartitionArray = objectMapper.createArrayNode();
        for(Map.Entry<String, List<Integer>> entry: entriesList){
            ObjectNode repartitionNode = objectMapper.createObjectNode();
            repartitionNode.put("developer", entry.getKey());

            ArrayNode assignedTicketsArray = objectMapper.createArrayNode();
            for (Integer ticketId : entry.getValue()) {
                assignedTicketsArray.add(ticketId);
            }

            repartitionNode.set("assignedTickets", assignedTicketsArray);
            repartitionArray.add(repartitionNode);
        }

        objNode.set("repartition", repartitionArray);

        return objNode;
    }
}
