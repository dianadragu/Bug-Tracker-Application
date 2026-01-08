package entities.users;

import entities.milestones.Milestone;
import entities.tickets.Ticket;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public abstract class User {
    private String username;
    private String email;
    private UserRole role;
    @Builder.Default
    private List<Milestone> milestones = new ArrayList<>();
}
