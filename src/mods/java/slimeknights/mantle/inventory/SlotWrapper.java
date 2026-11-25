package slimeknights.mantle.inventory;

import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotWrapper extends Slot {
   public final Slot parent;

   public SlotWrapper(Slot slot) {
      super(slot.inventory, slot.getSlotIndex(), slot.xPos, slot.yPos);
      this.parent = slot;
   }

   public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
      this.parent.onSlotChange(p_75220_1_, p_75220_2_);
   }

   public void onSlotChanged() {
      this.parent.onSlotChanged();
   }

   public boolean isItemValid(ItemStack stack) {
      return this.parent.isItemValid(stack);
   }

   public boolean canTakeStack(EntityPlayer playerIn) {
      return this.parent.canTakeStack(playerIn);
   }

   public void putStack(@Nonnull ItemStack stack) {
      this.parent.putStack(stack);
   }

   @Nonnull
   public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
      this.parent.onTake(playerIn, stack);
      return stack;
   }

   @Nonnull
   public ItemStack getStack() {
      return this.parent.getStack();
   }

   public boolean getHasStack() {
      return this.parent.getHasStack();
   }

   public int getSlotStackLimit() {
      return this.parent.getSlotStackLimit();
   }

   public int getItemStackLimit(ItemStack stack) {
      return this.parent.getItemStackLimit(stack);
   }

   @SideOnly(Side.CLIENT)
   public String getSlotTexture() {
      return this.parent.getSlotTexture();
   }

   @Nonnull
   public ItemStack decrStackSize(int amount) {
      return this.parent.decrStackSize(amount);
   }

   public boolean isHere(IInventory inv, int slotIn) {
      return this.parent.isHere(inv, slotIn);
   }

   @Nonnull
   public ResourceLocation getBackgroundLocation() {
      return this.parent.getBackgroundLocation();
   }

   public void setBackgroundName(@Nonnull String name) {
      this.parent.setBackgroundName(name);
   }

   @Nonnull
   public EaglerTextureAtlasSprite getBackgroundSprite() {
      return this.parent.getBackgroundSprite();
   }

   public void setBackgroundLocation(@Nonnull ResourceLocation texture) {
      this.parent.setBackgroundLocation(texture);
   }

   @SideOnly(Side.CLIENT)
   public boolean isEnabled() {
      return this.parent.isEnabled();
   }
}
