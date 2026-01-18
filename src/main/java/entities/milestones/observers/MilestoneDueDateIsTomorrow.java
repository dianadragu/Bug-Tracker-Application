package entities.milestones.observers;

import database.AppDatabase;
import entities.observer.Observer;
import entities.users.Developer;
import utils.DatesManagement;

public class MilestoneDueDateIsTomorrow implements Observer<MilestoneNotification> {
    @Override
    public void update(MilestoneNotification milestoneNotification) {
        if (milestoneNotification.getCurrentDate() != null && DatesManagement.isDueDateTomorrow(milestoneNotification.getDueDate(), milestoneNotification.getCurrentDate())) {

            AppDatabase database = AppDatabase.getInstance();

            for (Integer ticketId : milestoneNotification.getMilestone().getTickets()) {
                database.getTicketById(ticketId).changePriorityToCritical();
            }

            String msg = "Milestone " + milestoneNotification.getMilestoneName() +
                    " is due tomorrow. All unresolved tickets are now CRITICAL.";

            for (String dev : milestoneNotification.getMilestone().getAssignedDevs()) {
                Developer developer = (Developer) database.findUser(dev);
                if (!developer.getNotifications().contains(msg)) {
                    developer.getNotifications().add(msg);
                }
            }
        }
    }
}
