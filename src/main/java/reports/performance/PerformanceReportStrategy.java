package reports.performance;

import entities.users.Developer;

public interface PerformanceReportStrategy {
    public double calculatePerformance(DevStatistics stats);
}
