package entities.users;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class Developer extends User{
    private String hireDate;
    private ExpertiseArea expertiseArea;
    private DevSeniority seniority;
}
