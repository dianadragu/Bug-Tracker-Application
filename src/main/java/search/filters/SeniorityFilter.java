package search.filters;

import entities.users.DevSeniority;
import entities.users.Developer;


public class SeniorityFilter implements SearchStrategy<Developer> {
    private final DevSeniority seniority;

    public SeniorityFilter(DevSeniority seniority) {
        this.seniority = seniority;
    }

    @Override
    public boolean passFilter(Developer entity) {
        return seniority.equals(entity.getSeniority());
    }
}