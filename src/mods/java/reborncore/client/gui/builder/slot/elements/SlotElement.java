package reborncore.client.gui.builder.slot.elements;

import net.minecraftforge.items.ItemStackHandler;

public class SlotElement extends ElementBase {
   protected ItemStackHandler slotInventory;
   protected SlotType type;
   int slotId;
   int slotX;
   int slotY;

   public SlotElement(ItemStackHandler slotInventory, int slotId, int slotX, int slotY, SlotType type, int x, int y) {
      super(x, y, type.getSprite());
      this.type = type;
      this.slotInventory = slotInventory;
      this.slotId = slotId;
      this.slotX = slotX;
      this.slotY = slotY;
   }

   public SlotType getType() {
      return this.type;
   }

   public ItemStackHandler getSlotInventory() {
      return this.slotInventory;
   }

   public int getSlotId() {
      return this.slotId;
   }

   public int getSlotX() {
      return this.slotX;
   }

   public int getSlotY() {
      return this.slotY;
   }
}
