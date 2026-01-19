package reports;

import entities.tickets.Bug;
import entities.tickets.FeatureRequest;
import entities.tickets.UiFeedback;
import utils.MathUtils;

public class ResolutionEfficiencyReport implements TicketReportVisitor {
    private static final int BUG_MAXIMUM_VALUE = 70;
    private static final int BUG_FORMULA_PARAMETER = 10;
    private static final int FEATURE_REQUEST_MAXIMUM_VALUE = 20;
    private static final int UI_FEEDBACK_MAXIMUM_VALUE = 20;

    @Override
    public double visit(Bug bug) {
        double baseScore = ( bug.getFrequency().getScore() +
                bug.getSeverity().getScore() ) * BUG_FORMULA_PARAMETER /
                (double) bug.getDaysToResolve();

        return MathUtils.calculateImpactFinal(baseScore, BUG_MAXIMUM_VALUE);
    }

    @Override
    public double visit(FeatureRequest featureRequest) {
        double baseScore = (featureRequest.getBusinessValue().getScore() +
                featureRequest.getCustomerDemand().getScore()) /
                (double) featureRequest.getDaysToResolve();


        return MathUtils.calculateImpactFinal(baseScore, FEATURE_REQUEST_MAXIMUM_VALUE);
    }

    @Override
    public double visit(UiFeedback uiFeedback) {
        double baseScore = (uiFeedback.getBusinessValue().getScore() +
                            uiFeedback.getUsabilityScore()) /
                            (double) uiFeedback.getDaysToResolve();

        return MathUtils.calculateImpactFinal(baseScore, UI_FEEDBACK_MAXIMUM_VALUE);
    }
}
