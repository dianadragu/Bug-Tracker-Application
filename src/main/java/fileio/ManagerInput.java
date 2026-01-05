package fileio;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ManagerInput extends UserInput{
    private String hireDate;
    private List<String> subordinates;
}
