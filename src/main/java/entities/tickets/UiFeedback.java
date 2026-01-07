package entities.tickets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UiFeedback extends Ticket{
    private String uiElementsId;
    private BusinessValue businessValue;
    private Integer usabilityScore;
    private String screenshotUrl;
    private String suggestedFix;
}
