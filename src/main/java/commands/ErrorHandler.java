package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.AppDatabase;
import entities.users.User;
import entities.users.UserRole;
import fileio.CommandInput;
import fileio.UserInput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.CmdCommonOutput;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ErrorHandler {
    private CommandInput commandInput;
    private AppDatabase database;
    private String username;

    public ErrorHandler(CommandInput commandInput) {
        this.commandInput = commandInput;
        this.database = AppDatabase.getInstance();
        this.username = commandInput.getUsername();
    }

    public ObjectNode validateCmd(Command cmd)
    {
        User user = database.findUser(username);
        if (user == null) {
            ObjectNode objNode = CmdCommonOutput.toJson(commandInput);
            objNode.put("error", "The user " + username + " does not exist.");
            return objNode;
        }

        return cmd.accept(this);
    }

    public ObjectNode checkRolesForCmd(List<UserRole> roles) {
        UserRole currentUserRole = database.findUser(username).getRole();
        if (!roles.contains(currentUserRole)) {
            ObjectNode objNode = CmdCommonOutput.toJson(commandInput);
            objNode.put("error", "The user does not have permission to execute this command: required role " +
                    roles.toString()
                    .replace("[", "")
                    .replace("]", "")
                    + "; user role " + currentUserRole + ".");
            return objNode;
        }
        return null;
    }
}
