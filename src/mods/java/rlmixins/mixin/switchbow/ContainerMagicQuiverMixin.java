package rlmixins.mixin.switchbow;

import de.Whitedraco.switchbow.gui.ContainerMagicQuiver;
import de.Whitedraco.switchbow.gui.InventoryItemsMagicQuiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ContainerMagicQuiver.class})
public abstract class ContainerMagicQuiverMixin {
   @Shadow(
      remap = false
   )
   private ItemStack HoldItem;
   @Shadow(
      remap = false
   )
   private InventoryItemsMagicQuiver inventoryItemsMagicQuiver;

   public ContainerMagicQuiverMixin() {
   }

   @Overwrite
   public boolean canInteractWith(EntityPlayer player) {
      return this.inventoryItemsMagicQuiver.isUsableByPlayer(player) && !this.HoldItem.isEmpty() && player.inventory.hasItemStack(this.HoldItem);
   }
}
