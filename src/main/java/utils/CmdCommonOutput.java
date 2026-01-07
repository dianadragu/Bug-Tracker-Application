package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.CommandInput;

public class CmdCommonOutput {
    public static ObjectNode toJson(CommandInput cmdInput) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objNode = objectMapper.createObjectNode();
        objNode.put("command", cmdInput.getCommand());
        objNode.put("username", cmdInput.getUsername());
        objNode.put("timestamp", cmdInput.getTimestamp());
        return objNode;
    }
}
