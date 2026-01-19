package reports.performance;

import entities.users.Developer;
import utils.MathUtils;

public class SeniorPerformanceReport implements PerformanceReportStrategy{
    private static final double CLOSED_TICKETS_INTAKE = 0.5;
    private static final double HIGH_PRIORITY_TICKETS_INTAKE = 1.0;
    private static final double RESOLUTION_TIME_INTAKE = 0.5;

    @Override
    public double calculatePerformance(DevStatistics stats) {
        double score =  Math.max(0, CLOSED_TICKETS_INTAKE * stats.getClosedTickets() +
                        HIGH_PRIORITY_TICKETS_INTAKE * stats.getHighPriorityTickets() -
                        RESOLUTION_TIME_INTAKE * stats.getAverageResolutionTime()) +
                        SeniorityBonus.SENIOR.getBonusScore();
        return MathUtils.roundResult(score);
    }
}
