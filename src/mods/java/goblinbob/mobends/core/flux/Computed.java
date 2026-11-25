package goblinbob.mobends.core.flux;

import java.util.HashSet;
import java.util.Set;

public class Computed<T> implements IObservable<T>, IObserver {
   private boolean dirty = true;
   private T value;
   private IComputedExpression<T> expression;
   private Set<IObserver<T>> observers;

   public Computed(IComputedExpression<T> expression) {
      this.expression = expression;
      this.observers = new HashSet();
      ComputedDependencyHelper.dirtyComputedSet.add(this);
   }

   public T getValue() {
      ComputedDependencyHelper.linkDependency(this);
      if (this.dirty) {
         ComputedDependencyHelper.evaluatedStack.push(this);
         T newValue = this.expression.compute();

         for(IObserver<T> observer : this.observers) {
            observer.onChanged(newValue);
         }

         this.value = newValue;
         this.dirty = false;
         ComputedDependencyHelper.evaluatedStack.pop();
      }

      return this.value;
   }

   public Set<IObserver<T>> getObservers() {
      return this.observers;
   }

   public void onChanged(Object newValue) {
      this.dirty = true;
      ComputedDependencyHelper.dirtyComputedSet.add(this);
   }
}
