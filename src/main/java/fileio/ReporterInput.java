package fileio;

import entities.users.User;
import entities.users.UserLoaderVisitor;

public class ReporterInput extends UserInput{
    @Override
    public User accept(UserLoaderVisitor visitor) {
        return visitor.createUser(this);
    }
}
