package fileio;

import entities.users.User;
import entities.users.UserLoaderVisitor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeveloperInput extends UserInput{
    private String hireDate;
    private String seniority;
    private String expertiseArea;

    @Override
    public User accept(UserLoaderVisitor visitor) {
        return visitor.createUser(this);
    }
}
