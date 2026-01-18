package search;

import entities.users.Developer;
import entities.users.User;
import fileio.FiltersForSearchCmdInput;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import search.filters.SearchStrategy;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SearchContext<T> {
    private List<T> entitiesToFilter;
    private List<SearchStrategy<T>> searchStrategies;

    public SearchContext(List<T> entitiesToFilter, List<SearchStrategy<T>> searchStrategies) {
        this.entitiesToFilter = entitiesToFilter;
        this.searchStrategies = searchStrategies;
    }

    public List<T> applyFilters() {
        if (searchStrategies.isEmpty()) {
            return entitiesToFilter;
        }

        List<T> filteredEntities = new ArrayList<>();

        for (T entity : entitiesToFilter) {
            boolean passed = true;
            for (SearchStrategy<T> searchStrategy : searchStrategies) {
                if (!searchStrategy.passFilter(entity)) {
                    passed = false;
                    break;
                }
            }

            if (passed) {
                filteredEntities.add(entity);
            }
        }
        return filteredEntities;
    }
}
