package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.Deque;

@Getter
@Setter
public class CommandInvoker {
    private Deque<Command> history = new ArrayDeque<>();

    public ObjectNode execute(final Command command) {
        return command.execute();
    }
}
