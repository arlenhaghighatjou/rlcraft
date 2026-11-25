package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.INoteBlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent.Note;
import net.minecraftforge.event.world.NoteBlockEvent.Octave;

public class MCNoteBlockEvent extends MCBlockEvent implements INoteBlockEvent {
   private NoteBlockEvent event;

   public MCNoteBlockEvent(NoteBlockEvent event) {
      super(event);
      this.event = event;
   }

   public String getNote() {
      return this.event.getNote().toString();
   }

   public void setNote(String note) {
      this.event.setNote(Note.valueOf(note.toUpperCase()), this.event.getOctave());
   }

   public String getOctave() {
      return this.event.getOctave().toString();
   }

   public void setOctave(String octave) {
      this.event.setNote(this.event.getNote(), Octave.valueOf(octave.toUpperCase()));
   }

   public void setBoth(String note, String octave) {
      this.event.setNote(Note.valueOf(note.toUpperCase()), Octave.valueOf(octave.toUpperCase()));
   }

   public int getNoteId() {
      return this.event.getVanillaNoteId();
   }
}
