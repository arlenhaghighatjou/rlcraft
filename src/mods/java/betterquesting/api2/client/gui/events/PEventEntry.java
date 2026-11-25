package betterquesting.api2.client.gui.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class PEventEntry<T extends PanelEvent> {
   private final List<Consumer<PanelEvent>> listeners = new ArrayList();
   private final Class<T> cType;

   public PEventEntry(Class<T> type) {
      this.cType = type;
   }

   public void registerListener(@Nonnull Consumer<PanelEvent> consumer) {
      if (!this.listeners.contains(consumer)) {
         this.listeners.add(consumer);
      }
   }

   public void unregisterListener(@Nonnull Consumer<PanelEvent> consumer) {
      this.listeners.remove(consumer);
   }

   public void fire(@Nonnull PanelEvent event) {
      if (this.cType.isAssignableFrom(event.getClass())) {
         this.listeners.forEach((l) -> {
            l.accept(event);
         });
      }
   }
}
