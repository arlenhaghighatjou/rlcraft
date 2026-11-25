package goblinbob.mobends.core.flux;

import java.util.HashSet;
import java.util.Set;

public class Observable<T> implements IObservable<T> {
   private T value;
   private Set<IObserver<T>> observers;

   public Observable() {
      this.observers = new HashSet();
   }

   public Observable(T value) {
      this();
      this.value = value;
   }

   public void next(T newValue) {
      for(IObserver<T> observer : this.observers) {
         observer.onChanged(newValue);
      }

      this.value = newValue;
   }

   public T getValue() {
      ComputedDependencyHelper.linkDependency(this);
      return this.value;
   }

   public Set<IObserver<T>> getObservers() {
      return this.observers;
   }
}
