package fileio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import entities.tickets.TicketLoaderVisitor;
import entities.tickets.Ticket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UiFeedbackTicketInput extends ReportedTicketParamsInput{
    private String uiElementsId;
    private String businessValue;
    private Integer usabilityScore;
    private String screenshotUrl;
    private String suggestedFix;

    @Override
    public Ticket accept(TicketLoaderVisitor visitor) {
        return visitor.createTicket(this);
    }
}
