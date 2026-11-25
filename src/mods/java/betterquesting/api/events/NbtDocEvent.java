package betterquesting.api.events;

import betterquesting.api.nbt_doc.INbtDoc;
import net.minecraftforge.fml.common.eventhandler.Event;

public class NbtDocEvent extends Event {
   private final INbtDoc inJdoc;
   private INbtDoc outJdoc;

   public NbtDocEvent(INbtDoc jdoc) {
      this.inJdoc = jdoc;
      this.outJdoc = jdoc;
   }

   public INbtDoc getNbtDoc() {
      return this.inJdoc;
   }

   public void setNewDoc(INbtDoc jdoc) {
      this.outJdoc = jdoc;
   }

   public INbtDoc getNbtDocResult() {
      return this.outJdoc == null ? this.inJdoc : this.outJdoc;
   }
}
