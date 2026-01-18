package search.filters;

import entities.tickets.Ticket;
import entities.users.Developer;

import java.util.List;


public class KeywordsFilter implements SearchStrategy<Ticket> {
    private final List<String> keywords;

    public KeywordsFilter(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean passFilter(Ticket entity) {
        String title = entity.getTitle().toLowerCase();
        for (String keyword : keywords) {
            String searchingKeyword = keyword.toLowerCase();

            if (title.contains(searchingKeyword)) {
                return true;
            }

            if (entity.getDescription() != null) {
                String description = entity.getDescription().toLowerCase();
                if (description.contains(searchingKeyword)) {
                   return true;
                }
            }
        }
        return false;
    }
}