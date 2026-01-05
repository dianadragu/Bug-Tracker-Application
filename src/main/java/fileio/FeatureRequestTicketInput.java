package fileio;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureRequestTicketInput extends ReportedTicketParamsInput{
    private String ticketId;
    private String customerDemand;
}
