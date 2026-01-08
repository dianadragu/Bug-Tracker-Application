package commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.milestones.Milestone;
import entities.users.UserRole;
import fileio.CommandInput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.CmdCommonOutput;
import utils.StandardMilestoneOutput;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ViewMilestonesCmd implements Command{
    private CommandInput cmdInput;
    private AppDatabase appDatabase;

    public ViewMilestonesCmd(CommandInput cmdInput) {
        this.cmdInput = cmdInput;
        this.appDatabase = AppDatabase.getInstance();
    }

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }

        ObjectNode objNode = CmdCommonOutput.toJson(cmdInput);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        List<Milestone> sortedMilestones = new ArrayList<>(appDatabase.findUser(cmdInput.getUsername()).getMilestones());
        sortedMilestones.sort(new Comparator<Milestone>() {
            @Override
            public int compare(final Milestone o1, final Milestone o2) {
                int result = o1.getDueDate().compareTo(o2.getDueDate());
                if (result == 0) {
                    return o1.getName().compareTo(o2.getName());
                }
                return result;
            }
        });

        for (Milestone milestone : sortedMilestones) {
            arrayNode.add(StandardMilestoneOutput.toJson(milestone));
        }
        objNode.set("milestones", arrayNode);
        return objNode;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.MANAGER);
        acceptedRoles.add(UserRole.DEVELOPER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
