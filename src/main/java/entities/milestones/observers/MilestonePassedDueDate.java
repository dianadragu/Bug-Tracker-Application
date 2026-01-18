package entities.milestones.observers;

import database.AppDatabase;
import entities.milestones.Milestone;
import entities.observer.Observer;
import entities.users.Developer;
import utils.DatesManagement;

public class MilestonePassedDueDate implements Observer<MilestoneNotification> {
    @Override
    public void update(MilestoneNotification milestoneNotification) {
        if (milestoneNotification.getCanBeUnblocked() != null && milestoneNotification.getCanBeUnblocked()) {

            AppDatabase database = AppDatabase.getInstance();

            for (Integer ticketId : milestoneNotification.getMilestone().getTickets()) {
                database.getTicketById(ticketId).changePriorityToCritical();
            }

            String msg = "Milestone " + milestoneNotification.getMilestoneName() +
                    " was unblocked after due date. All active tickets are now CRITICAL.";

            for (String dev : milestoneNotification.getMilestone().getAssignedDevs()) {
                Developer developer = (Developer) database.findUser(dev);
                if (!developer.getNotifications().contains(msg)) {
                    developer.getNotifications().add(msg);
                }
            }
        }
    }
}
