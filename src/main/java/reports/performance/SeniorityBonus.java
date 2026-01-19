package reports.performance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeniorityBonus {
    JUNIOR(5),
    MID(15),
    SENIOR(30);

    private int bonusScore;
}
