package search.filters;

public interface SearchStrategy<T> {
    public boolean passFilter(T entity);
}
