package entities.observer;

import entities.observer.Observer;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
public class Subject<T> {
    @Builder.Default
    private List<Observer<T>> observers = new ArrayList<>();

    public void addObserver(Observer<T> o) {
        observers.add(o);
    }

    public void removeObserver(Observer<T> o) {
        observers.remove(o);
    }

    protected void notifyObservers(T notification) {
        for (Observer<T> o : observers) {
            o.update(notification);
        }
    }
}