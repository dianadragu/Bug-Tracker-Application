package fileio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class InputLoader {
    private List<UserInput> users;
    private List<CommandInput> commands;

    public InputLoader(String userInputPath, String commandInputPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.users = mapper.readValue(new File(userInputPath), new TypeReference<List<UserInput>>(){});
        this.commands = mapper.readValue(new File(commandInputPath), new TypeReference<List<CommandInput>>(){});
    }
}
