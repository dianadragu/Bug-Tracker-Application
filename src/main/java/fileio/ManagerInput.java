package fileio;

import entities.users.User;
import entities.users.UserLoaderVisitor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ManagerInput extends UserInput{
    private String hireDate;
    private List<String> subordinates;

    @Override
    public User accept(UserLoaderVisitor visitor) {
        return visitor.createUser(this);
    }
}
