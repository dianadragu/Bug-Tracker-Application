package entities.tickets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeatureCustomerDemand {
    LOW(1),
    MEDIUM(3),
    HIGH(6),
    VERY_HIGH(10),;

    private final int score;
}
