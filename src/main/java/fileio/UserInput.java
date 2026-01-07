package fileio;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import entities.users.User;
import entities.users.UserLoaderVisitor;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "role",
        visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ReporterInput.class, name = "REPORTER"),
    @JsonSubTypes.Type(value = DeveloperInput.class, name = "DEVELOPER"),
    @JsonSubTypes.Type(value = ManagerInput.class, name = "MANAGER")
})
@Getter
@Setter
public abstract class UserInput {
    private String username;
    private String role;
    private String email;

    public abstract User accept(UserLoaderVisitor visitor);
}
