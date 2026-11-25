package ivorius.reccomplex.gui.table.cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public abstract class TableCellPropertyDefault<P> extends TableCellDefault implements TableCellProperty<P> {
   protected P property;
   private List<Consumer<P>> listeners = new ArrayList();

   public TableCellPropertyDefault(String id, P value) {
      super(id);
      this.setPropertyValue(value);
   }

   public Consumer<P> addListener(Consumer<P> consumer) {
      this.listeners.add(consumer);
      return consumer;
   }

   public void removeListener(Consumer<P> listener) {
      this.listeners.remove(listener);
   }

   public List<Consumer<P>> getListeners() {
      return Collections.unmodifiableList(this.listeners);
   }

   protected void alertListenersOfChange() {
      for(Consumer<P> listener : this.listeners) {
         listener.accept(this.getPropertyValue());
      }

   }

   public P getPropertyValue() {
      return this.property;
   }

   public void setPropertyValue(P value) {
      this.property = value;
   }
}
