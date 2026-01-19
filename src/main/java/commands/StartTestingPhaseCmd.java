package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import database.WorkflowPhase;
import entities.milestones.Milestone;
import entities.milestones.MilestoneStatus;
import entities.users.UserRole;
import fileio.CommandInput;
import utils.CmdCommonOutput;

import java.util.ArrayList;
import java.util.List;

public class StartTestingPhaseCmd implements Command {
    private CommandInput cmdInput;

    public StartTestingPhaseCmd(final CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    };

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        AppDatabase database = AppDatabase.getInstance();

        boolean activeMilestones = false;
        for(Milestone milestone : database.getCreatedMilestones()) {
            if (milestone.getStatus() == MilestoneStatus.ACTIVE) {
                activeMilestones = true;
                break;
            }
        }

        if (activeMilestones) {
            ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
            objNode.put("error", "Cannot start a new testing phase.");
            return objNode;
        }

        database.setWorkflowPhase(WorkflowPhase.TESTING);
        return null;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.MANAGER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
