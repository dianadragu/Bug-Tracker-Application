package fileio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommandInput {
    private String command;
    private String username;
    private String timestamp;
    // Reporting
    private ReportedTicketParamsInput params;
    // milestone
    private String name;
    private String dueDate;
    private List<String> blockingFor;
    private List<Integer> tickets;
    private List<String> assignedDevs;
    // assign ticket
    private Integer ticketID;
    // comment
    private String comment;
    // search
    private FiltersForSearchCmdInput filters;
}
