package entities.milestones.observers;

import database.AppDatabase;
import entities.milestones.Milestone;
import entities.milestones.MilestoneStatus;
import entities.observer.Observer;
import entities.users.Developer;

public class MilestoneCompleted implements Observer<MilestoneNotification> {
    @Override
    public void update(MilestoneNotification milestoneNotification) {
        if (milestoneNotification.getMilestone().getStatus() == MilestoneStatus.COMPLETED) {
            AppDatabase database = AppDatabase.getInstance();

            for (String milestone : milestoneNotification.getMilestone().getBlockingFor()) {
                Milestone blockedMilestone = database.getMilestoneByName(milestone);
                blockedMilestone.unblockYourself();

                if (blockedMilestone.getOverdueBy() == 0) {
                    String msg = "Milestone " + milestone + " is now unblocked as ticket " +
                            milestoneNotification.getTicketId() + " has been CLOSED.";

                    for (String dev : blockedMilestone.getAssignedDevs()) {
                        Developer developer = (Developer) database.findUser(dev);
                        developer.getNotifications().add(msg);
                    }
                }
            }

            milestoneNotification.getMilestone().getBlockingFor().clear();
        }
    }
}
