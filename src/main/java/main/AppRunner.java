package main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import commands.CommandFactory;
import commands.CommandInvoker;
import database.AppDatabase;
import entities.users.User;
import entities.users.UserLoaderVisitor;
import fileio.CommandInput;
import fileio.InputLoader;
import fileio.UserInput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AppRunner {
    private AppDatabase database = AppDatabase.getInstance();
    private List<CommandInput> commands;
    private List<UserInput> users;
    private CommandInvoker invoker;

    public AppRunner(final InputLoader inputLoader) {
        this.invoker = new CommandInvoker();
        this.commands = inputLoader.getCommands();
        this.users = inputLoader.getUsers();
        loadUsers();
    }

    public List<ObjectNode> processCommands() {
        List<ObjectNode> outputNodes = new ArrayList<>();

        for (CommandInput command : commands) {
            try {
                Command cmd = new CommandFactory().createCommand(command);
                ObjectNode node = invoker.execute(cmd);
                if (node != null) {
                    outputNodes.add(node);
                }
            } catch (IllegalArgumentException e) {
                System.out.println(command.getCommand() + " is invalid!");
            }

        }
        return outputNodes;
    }

    public void loadUsers() {
        UserLoaderVisitor visitor = new UserLoaderVisitor();

        for (UserInput userInput : users) {
            User user = userInput.accept(visitor);
            database.getUsers().add(user);
        }
    }
}
