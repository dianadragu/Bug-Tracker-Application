package main;

import com.fasterxml.jackson.databind.node.ObjectNode;
import commands.Command;
import commands.CommandFactory;
import commands.CommandInvoker;
import database.AppDatabase;
import entities.tickets.Ticket;
import entities.users.User;
import entities.users.UserLoaderVisitor;
import fileio.CommandInput;
import fileio.InputLoader;
import fileio.UserInput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AppRunner {
    private AppDatabase database;
    private List<CommandInput> commands;
    private List<UserInput> users;
    private CommandInvoker invoker;

    public AppRunner(final InputLoader inputLoader) {
        this.invoker = new CommandInvoker();
        this.commands = inputLoader.getCommands();
        this.users = inputLoader.getUsers();
        this.database = AppDatabase.getInstance();
        database.clearDatabase();
        loadUsers();
    }

    public List<ObjectNode> processCommands() {
        List<ObjectNode> outputNodes = new ArrayList<>();

        for (CommandInput command : commands) {
            appAutomaticUpdates(command.getTimestamp());
            try {
                Command cmd = new CommandFactory().createCommand(command);
                ObjectNode node = invoker.execute(cmd);
                if (node != null) {
                    outputNodes.add(node);
                }
                System.out.print("Tichete în DB: [ ");
                for(Ticket ticket: database.getCreatedTickets()) {
                    System.out.print(ticket.getId() + " ");
                }
                System.out.println("]");
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

    public void appAutomaticUpdates(String currentTimestamp) {
        database.updateWorkflowPhase(LocalDate.parse(currentTimestamp));
        database.updateMilestones(LocalDate.parse(currentTimestamp));
    }
}
