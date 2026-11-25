package betterquesting.api2.client.gui.events;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PEventBroadcaster {
   public static PEventBroadcaster INSTANCE = new PEventBroadcaster();
   private final HashMap<Class<? extends PanelEvent>, PEventEntry<? extends PanelEvent>> entryList = new HashMap();

   public PEventBroadcaster() {
   }

   /** @deprecated */
   @Deprecated
   public void register(@Nonnull IPEventListener l, @Nonnull Class<? extends PanelEvent> type) {
      this.register(l::onPanelEvent, type);
   }

   public void register(@Nonnull Consumer<PanelEvent> consumer, @Nonnull Class<? extends PanelEvent> type) {
      PEventEntry<?> pe = (PEventEntry)this.entryList.computeIfAbsent(type, PEventEntry::new);
      pe.registerListener(consumer);
   }

   public void register(@Nonnull Consumer<PanelEvent> consumer, @Nonnull Iterable<Class<? extends PanelEvent>> type) {
      type.forEach((c) -> {
         PEventEntry<?> pe = (PEventEntry)this.entryList.computeIfAbsent(c, PEventEntry::new);
         pe.registerListener(consumer);
      });
   }

   /** @deprecated */
   @Deprecated
   public void unregister(IPEventListener l) {
      this.unregister(l::onPanelEvent);
   }

   public void unregister(@Nonnull Consumer<PanelEvent> consumer) {
      this.entryList.values().forEach((value) -> {
         value.unregisterListener(consumer);
      });
   }

   public boolean postEvent(@Nonnull PanelEvent event) {
      for(Entry<Class<? extends PanelEvent>, PEventEntry<? extends PanelEvent>> e : this.entryList.entrySet()) {
         if (((Class)e.getKey()).isAssignableFrom(event.getClass())) {
            ((PEventEntry)e.getValue()).fire(event);
         }
      }

      return event.canCancel() && event.isCancelled();
   }

   @SubscribeEvent
   public void onGuiOpened(GuiOpenEvent event) {
      this.entryList.clear();
   }
}
