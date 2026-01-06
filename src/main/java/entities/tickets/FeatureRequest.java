package entities.tickets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class FeatureRequest extends Ticket{
    private BusinessValue businessValue;
    private FeatureCustomerDemand customerDemand;
}
