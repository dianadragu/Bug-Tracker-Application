package fileio;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
public class UserInput {
    private String username;
    private String role;
    private String email;
}
