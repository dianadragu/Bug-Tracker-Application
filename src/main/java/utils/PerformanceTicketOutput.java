package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import entities.tickets.TicketPriority;
import entities.tickets.TicketType;
import reports.AppStability;
import reports.RiskReportMark;
import reports.performance.DevStatistics;

import java.util.List;
import java.util.Map;

public class PerformanceTicketOutput {
    public static ArrayNode toJson(List<DevStatistics> devStatistics) {

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode reportArray = objectMapper.createArrayNode();

        for (DevStatistics devStat : devStatistics) {
            ObjectNode devStatsNode = objectMapper.createObjectNode();
            devStatsNode.put("username", devStat.getDev().getUsername());
            devStatsNode.put("closedTickets", devStat.getClosedTickets());
            devStatsNode.put("averageResolutionTime", devStat.getAverageResolutionTime());
            devStatsNode.put("performanceScore", devStat.getDev().getPerformanceScore());
            devStatsNode.put("seniority",  devStat.getDev().getSeniority().toString());
            reportArray.add(devStatsNode);
        }

        return reportArray;
    }
}
