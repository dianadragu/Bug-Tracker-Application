package reports;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RiskReportMark {
    NEGLIGIBLE(0, 24),
    MODERATE(25, 49),
    SIGNIFICANT(50, 74),
    MAJOR(75, 100);

    private final int lowerLimit;
    private final int upperLimit;

    public static RiskReportMark getMark(double value) {
        for (RiskReportMark mark : values()) {
            if (value >= mark.lowerLimit && value <= mark.upperLimit) {
                return mark;
            }
        }
        return null;
    }
}
