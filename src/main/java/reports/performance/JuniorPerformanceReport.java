package reports.performance;

import entities.tickets.TicketType;
import entities.users.Developer;
import lombok.Builder;
import utils.MathUtils;

public class JuniorPerformanceReport implements PerformanceReportStrategy{
    private static final double CLOSED_TICKETS_INTAKE = 0.5;
    @Override
    public double calculatePerformance(DevStatistics stats) {
        double ticketDiversityFactor = DevStatistics.ticketDiversityFactor(stats.getTicketsTypes().get(TicketType.BUG),
                                                                            stats.getTicketsTypes().get(TicketType.FEATURE_REQUEST),
                                                                            stats.getTicketsTypes().get(TicketType.UI_FEEDBACK));
        double score = Math.max(0.0,  CLOSED_TICKETS_INTAKE * stats.getClosedTickets() - ticketDiversityFactor) + SeniorityBonus.JUNIOR.getBonusScore();
        return MathUtils.roundResult(score);
    }
}
