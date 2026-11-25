package slimeknights.mantle.inventory;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SlotCraftingCustom extends SlotCrafting {
   private final InventoryCrafting craftMatrix;
   private final IContainerCraftingCustom callback;

   public SlotCraftingCustom(IContainerCraftingCustom callback, EntityPlayer player, InventoryCrafting craftingInventory, IInventory craftResult, int slotIndex, int xPosition, int yPosition) {
      super(player, craftingInventory, craftResult, slotIndex, xPosition, yPosition);
      this.craftMatrix = craftingInventory;
      this.callback = callback;
   }

   @Nonnull
   public ItemStack onTake(EntityPlayer playerIn, @Nonnull ItemStack stack) {
      FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, this.craftMatrix);
      this.onCrafting(stack);
      this.callback.onCrafting(playerIn, stack, this.craftMatrix);
      return stack;
   }
}
