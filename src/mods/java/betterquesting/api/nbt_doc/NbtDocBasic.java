package betterquesting.api.nbt_doc;

import betterquesting.api.events.NbtDocEvent;
import net.minecraftforge.common.MinecraftForge;

public class NbtDocBasic implements INbtDoc {
   private final INbtDoc parent;
   private final String prefix;

   public NbtDocBasic(INbtDoc parent, String prefix) {
      this.parent = parent;
      this.prefix = prefix;
   }

   public String getUnlocalisedTitle() {
      return this.prefix + ".name";
   }

   public String getUnlocalisedName(String key) {
      return this.prefix + "." + key + ".name";
   }

   public String getUnlocalisedDesc(String key) {
      return this.prefix + "." + key + ".desc";
   }

   public INbtDoc getParent() {
      return this.parent;
   }

   public INbtDoc getChild(String child) {
      NbtDocEvent event = new NbtDocEvent(new NbtDocBasic(this, this.prefix + "." + child));
      MinecraftForge.EVENT_BUS.post(event);
      return event.getNbtDocResult();
   }
}
