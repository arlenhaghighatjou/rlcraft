package vazkii.quark.base.capability;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import vazkii.quark.api.capability.IBoatBanner;

public class BoatBanner implements IBoatBanner, INBTSerializable<NBTTagCompound> {
   @Nonnull
   private ItemStack stack = ItemStack.EMPTY;

   public BoatBanner() {
   }

   public void setStack(@Nonnull ItemStack stack) {
      this.stack = stack;
   }

   @Nonnull
   public ItemStack getStack() {
      return this.stack;
   }

   public NBTTagCompound serializeNBT() {
      return this.getStack().serializeNBT();
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.getStack().deserializeNBT(nbt);
   }
}
