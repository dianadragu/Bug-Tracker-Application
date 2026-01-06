package entities.milestones;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Milestone {
    private String name;
    private List<String> blockingFor;
    private LocalDate dueDate;
    private List<Integer> tickets;
    private List<String> assignedDevs;

    public Milestone(String name, List<String> blockingFor, LocalDate dueDate, List<Integer> tickets, List<String> assignedDevs) {
        this.name = name;
        this.blockingFor = blockingFor;
        this.dueDate = dueDate;
        this.tickets = tickets;
        this.assignedDevs = assignedDevs;
    }
}
