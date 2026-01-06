package entities.users;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class User {
    private String username;
    private String email;
    private UserRole role;
}
