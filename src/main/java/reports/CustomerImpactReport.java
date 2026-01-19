package reports;

import entities.tickets.Bug;
import entities.tickets.FeatureRequest;
import entities.tickets.UiFeedback;
import utils.MathUtils;

public class CustomerImpactReport implements TicketReportVisitor{
    private static final int BUG_MAXIMUM_VALUE = 48;
    private static final int FEATURE_REQUEST_MAXIMUM_VALUE = 100;
    private static final int UI_FEEDBACK_MAXIMUM_VALUE = 100;

    @Override
    public double visit(Bug bug) {
        double baseScore = bug.getFrequency().getScore() *
                        bug.getBusinessPriority().getScore() *
                        bug.getSeverity().getScore();

        return MathUtils.calculateImpactFinal(baseScore, BUG_MAXIMUM_VALUE);
    }

    @Override
    public double visit(FeatureRequest featureRequest) {
        double baseScore = featureRequest.getBusinessValue().getScore() *
                        featureRequest.getCustomerDemand().getScore();

        return MathUtils.calculateImpactFinal(baseScore, FEATURE_REQUEST_MAXIMUM_VALUE);
    }

    @Override
    public double visit(UiFeedback uiFeedback) {
        double baseScore = uiFeedback.getBusinessValue().getScore() *
                            uiFeedback.getUsabilityScore();

        return MathUtils.calculateImpactFinal(baseScore, UI_FEEDBACK_MAXIMUM_VALUE);
    }
}
