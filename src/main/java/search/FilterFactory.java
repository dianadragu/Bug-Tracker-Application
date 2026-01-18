package search;

import entities.tickets.Ticket;
import entities.tickets.TicketPriority;
import entities.tickets.TicketType;
import entities.users.DevSeniority;
import entities.users.Developer;
import entities.users.ExpertiseArea;
import entities.users.User;
import fileio.FiltersForSearchCmdInput;
import search.filters.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FilterFactory {
    private FiltersForSearchCmdInput filters;

    public FilterFactory(FiltersForSearchCmdInput filters) {
        this.filters = filters;
    }

    public List<SearchStrategy<Ticket>> getStrategiesForTicket(User user) {
        List<SearchStrategy<Ticket>> strategies = new ArrayList<>();

        if (filters.getBusinessPriority() != null) {
            strategies.add(new BusinessPriorityFilter(TicketPriority.valueOf(filters.getBusinessPriority())));
        }

        if (filters.getType() != null) {
            strategies.add(new TypeFilter(TicketType.valueOf(filters.getType())));
        }

        if (filters.getCreatedAt() != null) {
            strategies.add(new CreatedAtFilter(LocalDate.parse(filters.getCreatedAt())));
        }

        if (filters.getCreatedBefore() != null) {
            strategies.add(new CreatedBeforeFilter(LocalDate.parse(filters.getCreatedBefore())));
        }

        if (filters.getCreatedAfter() != null) {
            strategies.add(new CreatedAfterFilter(LocalDate.parse(filters.getCreatedAfter())));
        }

        if (filters.getAvailableForAssignment() != null) {
            Developer dev = (Developer) user;
            strategies.add(new AvailableForAssignmentFilter(filters.getAvailableForAssignment(), dev));
        }

        if (filters.getKeywords() != null) {
            strategies.add(new KeywordsFilter(filters.getKeywords()));
        }

        return strategies;
    }

    public List<SearchStrategy<Developer>> getStrategiesForDeveloper() {
        List<SearchStrategy<Developer>> strategies = new ArrayList<>();

        if (filters.getExpertiseArea() != null) {
            strategies.add(new ExpertiseAreaFilter(ExpertiseArea.valueOf(filters.getExpertiseArea())));
        }

        if (filters.getSeniority() != null) {
            strategies.add(new SeniorityFilter(DevSeniority.valueOf(filters.getSeniority())));
        }

        if (filters.getPerformanceScoreAbove() != null) {
            strategies.add(new PerformanceScoreAboveFilter(filters.getPerformanceScoreAbove()));
        }

        if (filters.getPerformanceScoreBelow() != null) {
            strategies.add(new PerformanceScoreBelowFilter(filters.getPerformanceScoreBelow()));
        }

        return strategies;
    }
}
