package goblinbob.mobends.core.client.gui;

import java.util.List;

public interface IObservable {
   List<IChangeListener> getChangeListeners();

   default void addListener(IChangeListener listener) {
      this.getChangeListeners().add(listener);
   }

   default void notifyChanged() {
      for(IChangeListener listener : this.getChangeListeners()) {
         listener.handleChange(this);
      }

   }
}
