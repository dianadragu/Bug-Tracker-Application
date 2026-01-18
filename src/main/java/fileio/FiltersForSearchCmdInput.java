package fileio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FiltersForSearchCmdInput {
    // tickets
    private String searchType;
    private String businessPriority;
    private String type;
    private String createdAt;
    private String createdBefore;
    private String createdAfter;
    private Boolean availableForAssignment;
    // devs
    private List<String> keywords;
    private String expertiseArea;
    private String seniority;
    private Double performanceScoreAbove;
    private Double performanceScoreBelow;
}
