package entities.tickets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessValue {
    S(1),
    M(3),
    L(6),
    XL(10);

    private final int score;
}
