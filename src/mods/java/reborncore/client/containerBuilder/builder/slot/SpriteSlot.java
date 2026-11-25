package reborncore.client.containerBuilder.builder.slot;

import javax.annotation.Nullable;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SpriteSlot extends FilteredSlot {
   private final String spriteName;
   int stacksize;

   public SpriteSlot(IInventory inventory, int index, int xPosition, int yPosition, String sprite, int stacksize) {
      super(inventory, index, xPosition, yPosition);
      this.spriteName = sprite;
      this.stacksize = stacksize;
   }

   public SpriteSlot(IInventory inventory, int index, int xPosition, int yPosition, String sprite) {
      this(inventory, index, xPosition, yPosition, sprite, 64);
   }

   public int getSlotStackLimit() {
      return this.stacksize;
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public String getSlotTexture() {
      return this.spriteName;
   }
}
