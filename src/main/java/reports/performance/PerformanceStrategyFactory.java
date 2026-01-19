package reports.performance;

import entities.users.DevSeniority;

public class PerformanceStrategyFactory {
    private DevSeniority devSeniority;

    public PerformanceStrategyFactory(DevSeniority devSeniority) {
        this.devSeniority = devSeniority;
    }

    public PerformanceReportStrategy createStrategy(){
        switch (devSeniority){
            case JUNIOR:
                return new JuniorPerformanceReport();
            case MID:
                return new MidPerformanceReport();
            case SENIOR:
                return new SeniorPerformanceReport();
            default:
                throw new IllegalArgumentException("Unknown DevSeniority");
        }
    }
}
