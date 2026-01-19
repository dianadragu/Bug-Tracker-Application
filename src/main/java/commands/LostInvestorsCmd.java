package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.users.UserRole;
import fileio.CommandInput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LostInvestorsCmd implements Command {
    private CommandInput cmdInput;

    public LostInvestorsCmd(final CommandInput cmdInput) {
        this.cmdInput = cmdInput;
    };

    @Override
    public ObjectNode execute() {
        ErrorHandler errorHandler = new ErrorHandler(cmdInput);
        if (errorHandler.validateCmd(this) != null) {
            return errorHandler.validateCmd(this);
        }
        AppDatabase.getInstance().setAppStopped(true);
        return null;
    }

    @Override
    public ObjectNode accept(ErrorHandler errorHandler) {
        List<UserRole> acceptedRoles = new ArrayList<>();
        acceptedRoles.add(UserRole.MANAGER);
        return errorHandler.checkRolesForCmd(acceptedRoles);
    }
}
