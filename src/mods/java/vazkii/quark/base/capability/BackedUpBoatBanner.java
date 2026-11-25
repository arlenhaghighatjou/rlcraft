package vazkii.quark.base.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vazkii.quark.api.capability.IBoatBanner;

public class BackedUpBoatBanner implements IBoatBanner {
   private static final String TAG_BANNER = "quark:banner";
   @Nonnull
   private final EntityBoat boat;
   @Nonnull
   private ItemStack cacheStack = ItemStack.EMPTY;
   @Nullable
   private NBTTagCompound cacheCompound = null;

   public BackedUpBoatBanner(@Nonnull EntityBoat boat) {
      this.boat = boat;
   }

   public void setStack(@Nonnull ItemStack stack) {
      NBTTagCompound itemCompound = stack.serializeNBT();
      this.boat.getEntityData().setTag("quark:banner", itemCompound);
      this.cacheCompound = itemCompound;
      this.cacheStack = stack;
   }

   private void setStack(@Nonnull NBTTagCompound itemCompound) {
      ItemStack stack = new ItemStack(itemCompound);
      this.boat.getEntityData().setTag("quark:banner", itemCompound);
      this.cacheCompound = itemCompound;
      this.cacheStack = stack;
   }

   @Nonnull
   public ItemStack getStack() {
      NBTTagCompound itemCompound = this.boat.getEntityData().getCompoundTag("quark:banner");
      if (itemCompound != this.cacheCompound) {
         this.setStack(itemCompound);
      }

      return this.cacheStack;
   }
}
