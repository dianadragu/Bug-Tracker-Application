package search.filters;

import entities.users.Developer;


public class PerformanceScoreAboveFilter implements SearchStrategy<Developer> {
    private final double score;

    public PerformanceScoreAboveFilter(double score) {
        this.score = score;
    }

    @Override
    public boolean passFilter(Developer entity) {
        return score > entity.getPerformanceScore();
    }
}