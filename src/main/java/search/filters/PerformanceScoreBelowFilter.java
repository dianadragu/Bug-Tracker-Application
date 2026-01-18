package search.filters;

import entities.users.Developer;


public class PerformanceScoreBelowFilter implements SearchStrategy<Developer> {
    private final double score;

    public PerformanceScoreBelowFilter(double score) {
        this.score = score;
    }

    @Override
    public boolean passFilter(Developer entity) {
        return score < entity.getPerformanceScore();
    }
}