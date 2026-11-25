package codersafterdark.compatskills.common.compats.tinkersconstruct.toollocks;

import codersafterdark.reskillable.api.data.LockKey;
import net.minecraft.item.Item;

public class ToolTypeLockKey implements LockKey {
   private final Item item;

   public ToolTypeLockKey(Item item) {
      this.item = item;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof ToolTypeLockKey && this.item == ((ToolTypeLockKey)o).item;
   }

   public int hashCode() {
      return this.item == null ? super.hashCode() : this.item.hashCode();
   }
}
