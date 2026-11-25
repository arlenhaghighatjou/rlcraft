package rustic.common.blocks.fluids;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public abstract class FluidDrinkable extends Fluid {
   public FluidDrinkable(String fluidName, ResourceLocation still, ResourceLocation flowing) {
      super(fluidName, still, flowing);
   }

   public abstract void onDrank(@Nonnull World var1, @Nonnull EntityPlayer var2, @Nonnull ItemStack var3, @Nonnull FluidStack var4);
}
