package entities.observer;

public interface Observer<T> {
    public void update(T notification);
}
