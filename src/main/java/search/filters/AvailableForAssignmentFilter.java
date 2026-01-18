package search.filters;

import entities.milestones.Milestone;
import entities.tickets.Ticket;
import entities.users.Developer;

public class AvailableForAssignmentFilter implements SearchStrategy<Ticket> {
    private final boolean availability;
    private final Developer dev;

    public AvailableForAssignmentFilter(boolean availability, Developer dev) {
        this.availability = availability;
        this.dev = dev;
    }

    @Override
    public boolean passFilter(Ticket entity) {
        if (availability) {
            if (!entity.getExpertiseAreas().contains(dev.getExpertiseArea())) {
                return false;
            }

            if (!entity.getRequiredSeniority().contains(dev.getSeniority())) {
                return false;
            }

            for (Milestone milestone : dev.getMilestones()) {
                if (milestone.getTickets().contains(entity.getId()) && milestone.isBlocked()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
