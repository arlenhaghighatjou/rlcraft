package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.NoteBlockChangeEvent;
import net.minecraftforge.event.world.NoteBlockEvent.Change;

public class MCNoteBlockChangeEvent extends MCNoteBlockEvent implements NoteBlockChangeEvent {
   private Change event;

   public MCNoteBlockChangeEvent(Change event) {
      super(event);
      this.event = event;
   }

   public String getOldNote() {
      return this.event.getOldNote().toString();
   }

   public String getOldOctave() {
      return this.event.getOldOctave().toString();
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
