package entities.tickets;

import entities.users.ExpertiseArea;
import fileio.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class TicketLoaderVisitor {

    public Ticket createTicket(BugTicketInput input) {
        Ticket ticket = Bug.builder()
                .type(input.getType())
                .title(input.getTitle())
                .businessPriority(anonymousTicket(input.getReportedBy(), TicketPriority.valueOf(input.getBusinessPriority())))
                .status(TicketStatus.OPEN)
                .expertiseArea(ExpertiseArea.valueOf(input.getExpertiseArea()))
                .description(input.getDescription())
                .reportedBy(input.getReportedBy())
                .expectedBehavior(input.getExpectedBehavior())
                .actualBehavior(input.getActualBehavior())
                .frequency(BugFrequency.valueOf(input.getFrequency()))
                .severity(BugSeverity.valueOf(input.getSeverity()))
                .environment(input.getEnvironment())
                .errorCode(input.getErrorCode())
                .build();
        return ticket;
    }

    public Ticket createTicket(UiFeedbackTicketInput input) {
        Ticket ticket = UiFeedback.builder()
                .type(input.getType())
                .title(input.getTitle())
                .businessPriority(anonymousTicket(input.getReportedBy(), TicketPriority.valueOf(input.getBusinessPriority())))
                .status(TicketStatus.OPEN)
                .expertiseArea(ExpertiseArea.valueOf(input.getExpertiseArea()))
                .description(input.getDescription())
                .reportedBy(input.getReportedBy())
                .uiElementsId(input.getUiElementsId())
                .businessValue(BusinessValue.valueOf(input.getBusinessValue()))
                .usabilityScore(input.getUsabilityScore())
                .screenshotUrl(input.getScreenshotUrl())
                .suggestedFix(input.getSuggestedFix())
                .build();
        return ticket;
    }

    public Ticket createTicket(FeatureRequestTicketInput input) {
        Ticket ticket = FeatureRequest.builder()
                .type(input.getType())
                .title(input.getTitle())
                .businessPriority(anonymousTicket(input.getReportedBy(), TicketPriority.valueOf(input.getBusinessPriority())))
                .status(TicketStatus.OPEN)
                .expertiseArea(ExpertiseArea.valueOf(input.getExpertiseArea()))
                .description(input.getDescription())
                .reportedBy(input.getReportedBy())
                .businessValue(BusinessValue.valueOf(input.getBusinessValue()))
                .customerDemand(FeatureCustomerDemand.valueOf(input.getCustomerDemand()))
                .build();
        return ticket;
    };

    public TicketPriority anonymousTicket(String reportedBy, TicketPriority priority) {
        if (Objects.equals(reportedBy, "")) {
            return TicketPriority.LOW;
        }
        return priority;
    }
}
