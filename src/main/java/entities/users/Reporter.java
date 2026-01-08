package entities.users;

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
public class Reporter extends User{
    @Builder.Default
    List<Ticket> reportedTickets = new ArrayList<>();

}
