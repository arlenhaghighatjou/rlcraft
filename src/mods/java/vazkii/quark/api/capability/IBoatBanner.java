package vazkii.quark.api.capability;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public interface IBoatBanner {
   @CapabilityInject(IBoatBanner.class)
   Capability<IBoatBanner> CAPABILITY = null;

   static boolean hasBoatBanner(Entity entity) {
      return entity.hasCapability(CAPABILITY, (EnumFacing)null);
   }

   static IBoatBanner getBoatBanner(Entity entity) {
      return (IBoatBanner)entity.getCapability(CAPABILITY, (EnumFacing)null);
   }

   void setStack(@Nonnull ItemStack var1);

   @Nonnull
   ItemStack getStack();
}
