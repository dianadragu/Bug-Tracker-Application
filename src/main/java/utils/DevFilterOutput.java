package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.users.Developer;

public class DevFilterOutput {
    public static ObjectNode toJson(Developer dev) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objNode = objectMapper.createObjectNode();

        objNode.put("username", dev.getUsername());
        objNode.put("expertiseArea", dev.getExpertiseArea().toString());
        objNode.put("seniority", dev.getSeniority().toString());
        objNode.put("performanceScore", dev.getPerformanceScore());
        objNode.put("hireDate", dev.getHireDate());

        return objNode;
    }
}
