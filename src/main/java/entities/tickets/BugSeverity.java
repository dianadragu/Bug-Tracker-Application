package entities.tickets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BugSeverity {
    MINOR(1),
    MODERATE(2),
    SEVERE(3);

    private final int score;
}
