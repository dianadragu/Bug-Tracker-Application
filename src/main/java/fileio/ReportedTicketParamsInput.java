package fileio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BugTicketInput.class, name = "BUG"),
        @JsonSubTypes.Type(value = FeatureRequestTicketInput.class, name = "FEATURE_REQUEST"),
        @JsonSubTypes.Type(value = UiFeedbackTicketInput.class, name = "UI_FEEDBACK")
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ReportedTicketParamsInput {
    private Integer id;
    private String type;
    private String title;
    private String businessPriority;
    private String status;
    private String expertiseArea;
    private String description;
    private String reportedBy;
}
