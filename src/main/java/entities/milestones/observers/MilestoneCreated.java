package entities.milestones.observers;

import database.AppDatabase;
import entities.observer.Observer;
import entities.users.Developer;

public class MilestoneCreated implements Observer<MilestoneNotification> {
    @Override
    public void update(MilestoneNotification milestoneNotification) {
        if (milestoneNotification.getCurrentDate() != null &&
            milestoneNotification.getCurrentDate().equals(milestoneNotification.getMilestone().getCreatedAt())) {
            String msg = "New milestone " + milestoneNotification.getMilestoneName() +
                    " has been created with due date " + milestoneNotification.getDueDate() + ".";

            AppDatabase database = AppDatabase.getInstance();
            for (String dev : milestoneNotification.getMilestone().getAssignedDevs()) {
                Developer developer = (Developer) database.findUser(dev);
                if (!developer.getNotifications().contains(msg)) {
                    developer.getNotifications().add(msg);
                }
            }
        }
    }
}
