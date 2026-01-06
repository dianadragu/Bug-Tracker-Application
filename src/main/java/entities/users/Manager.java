package entities.users;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
public class Manager extends User{
    private String hireDate;
    private List<String> subordinates;
}
