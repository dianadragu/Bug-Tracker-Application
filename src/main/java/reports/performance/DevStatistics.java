package reports.performance;

import entities.tickets.Ticket;
import entities.tickets.TicketPriority;
import entities.tickets.TicketStatus;
import entities.tickets.TicketType;
import entities.users.DevSeniority;
import entities.users.Developer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.DatesManagement;
import utils.MathUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class DevStatistics {
    private Developer dev;
    private int closedTickets;
    private int highPriorityTickets;
    private double averageResolutionTime;
    private LocalDate currentDate;
    private Map<TicketType, Integer> ticketsTypes;

    public void saveStats(Developer dev, LocalDate currentDate) {
        this.currentDate = currentDate;
        this.dev = dev;
        this.closedTickets = 0;
        this.averageResolutionTime = 0.0;
        this.ticketsTypes = new HashMap<>();
        this.highPriorityTickets = 0;
        ticketsTypes.put(TicketType.BUG, 0);
        ticketsTypes.put(TicketType.FEATURE_REQUEST, 0);
        ticketsTypes.put(TicketType.UI_FEEDBACK, 0);
        calculateParams();
    }

    public void calculateParams() {
        for(Ticket ticket: dev.getAssignedTickets()) {
            if (ticket.getStatus() == TicketStatus.CLOSED && DatesManagement.isLastMonth(LocalDate.parse(ticket.getClosedAt()), currentDate)) {
                closedTickets++;
                averageResolutionTime += DatesManagement.getDaysPassed(LocalDate.parse(ticket.getAssignedAt()), LocalDate.parse(ticket.getSolvedAt()));
                ticketsTypes.put(ticket.getType(), ticketsTypes.get(ticket.getType()) + 1);


                if (ticket.getBusinessPriority() == TicketPriority.HIGH || ticket.getBusinessPriority() == TicketPriority.CRITICAL) {
                    highPriorityTickets++;
                }
            }
        }
        if (averageResolutionTime > 0.0 && closedTickets > 0) {
            averageResolutionTime = MathUtils.roundResult(averageResolutionTime / closedTickets);
        }

        if (closedTickets == 0) {
            dev.setHasClosedTickets(false);
        }
    }

    public static double averageResolvedTicketType(int bug, int feature, int ui) {
        return (bug + feature + ui) / 3.0;
    }

    public static double standardDeviation(int bug, int feature, int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        double variance = (Math.pow(bug - mean, 2) + Math.pow(feature - mean, 2) + Math.pow(ui - mean, 2)) / 3.0;
        return Math.sqrt(variance);
    }

    public static double ticketDiversityFactor(int bug, int feature, int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);

        if (mean == 0.0) {
            return 0.0;
        }

        double std = standardDeviation(bug, feature, ui);
        return std / mean;
    }
}
