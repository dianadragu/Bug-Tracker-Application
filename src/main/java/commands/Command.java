package commands;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Command {
    public ObjectNode execute();

    public ObjectNode accept(ErrorHandler errorHandler);
}
