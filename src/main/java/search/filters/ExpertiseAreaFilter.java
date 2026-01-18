package search.filters;

import entities.users.Developer;
import entities.users.ExpertiseArea;


public class ExpertiseAreaFilter implements SearchStrategy<Developer> {
    private final ExpertiseArea expertiseArea;

    public ExpertiseAreaFilter(ExpertiseArea expertiseArea) {
        this.expertiseArea = expertiseArea;
    }

    @Override
    public boolean passFilter(Developer entity) {
        return expertiseArea.equals(entity.getExpertiseArea());
    }
}