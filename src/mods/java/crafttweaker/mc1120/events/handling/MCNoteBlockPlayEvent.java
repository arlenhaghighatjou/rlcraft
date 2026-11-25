package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.NoteBlockPlayEvent;
import net.minecraftforge.event.world.NoteBlockEvent.Instrument;
import net.minecraftforge.event.world.NoteBlockEvent.Play;

public class MCNoteBlockPlayEvent extends MCNoteBlockEvent implements NoteBlockPlayEvent {
   private Play event;

   public MCNoteBlockPlayEvent(Play event) {
      super(event);
      this.event = event;
   }

   public String getInstrument() {
      return this.event.getInstrument().toString();
   }

   public void setInstrument(String instrument) {
      this.event.setInstrument(Instrument.valueOf(instrument.toUpperCase()));
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
