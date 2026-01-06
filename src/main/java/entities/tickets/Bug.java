package entities.tickets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Bug extends Ticket{
    private String expectedBehavior;
    private String actualBehavior;
    private BugFrequency frequency;
    private BugSeverity severity;
    private String environment;
    private int errorCode;
}
