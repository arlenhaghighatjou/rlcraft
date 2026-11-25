package org.jwaresoftware.mcmods.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class Fluids {
   public static final int BOTTLES_PER_BUCKET = 4;
   public static final int MILLIBUCKETS_PER_BOTTLE = 250;
   public static final String MILK_FLUID_NAME = "milk";

   public static final boolean isEmpty(@Nullable IFluidTankProperties[] fpList) {
      IFluidTankProperties out = getFirstNonEmpty(fpList);
      return out == null;
   }

   @Nullable
   public static final IFluidTankProperties getFirstNonEmpty(@Nullable IFluidTankProperties[] fpList) {
      if (fpList != null && fpList.length > 0) {
         for(IFluidTankProperties fp : fpList) {
            FluidStack fluid = fp.getContents();
            if (fp != EmptyFluidHandler.EMPTY_TANK_PROPERTIES && fluid != null && fluid.amount > 0) {
               return fp;
            }
         }
      }

      return null;
   }

   @Nullable
   public static final Fluid getFirstFluid(@Nullable IFluidTankProperties[] fpList) {
      IFluidTankProperties out = getFirstNonEmpty(fpList);
      FluidStack f = out != null ? out.getContents() : null;
      return f != null ? f.getFluid() : null;
   }

   public static final boolean isPotableWater(String fluidName) {
      return fluidName != null && ("potable_water".equals(fluidName) || "purified_water".equals(fluidName) || "distilled_water".equals(fluidName) || "distilledwater".equals(fluidName));
   }

   public static final boolean isPotableWater(@Nullable FluidStack supplied) {
      return supplied != null && supplied.getFluid() != null && isPotableWater(supplied.getFluid().getName());
   }

   public static final boolean isWater(String fluidName) {
      return fluidName != null && (FluidRegistry.WATER.getName().equals(fluidName) || isPotableWater(fluidName) || "rain_water".equals(fluidName));
   }

   public static final boolean isWater(@Nullable FluidStack supplied) {
      return supplied != null && supplied.getFluid() != null && isWater(supplied.getFluid().getName());
   }

   public static final boolean isMilk(@Nullable String fluidName) {
      return fluidName != null && ("milk".equals(fluidName) || "cow_milk".equals(fluidName));
   }

   public static final boolean isMilk(@Nullable FluidStack supplied) {
      return supplied != null && supplied.getFluid() != null && isMilk(supplied.getFluid().getName());
   }

   public static final boolean isPlantMilk(@Nullable String fluidName) {
      return fluidName != null && ("soymilk".equals(fluidName) || "soy_milk".equals(fluidName) || "almond_milk".equals(fluidName) || "coconut_milk".equals(fluidName));
   }

   public static final boolean isMilkLike(@Nullable String fluidName) {
      return fluidName != null && (isMilk(fluidName) || isPlantMilk(fluidName));
   }

   public static final boolean isMilkLike(@Nullable FluidStack supplied) {
      return supplied != null && supplied.getFluid() != null && isMilkLike(supplied.getFluid().getName());
   }

   public static final boolean isLiquidXp(String fluidName) {
      return fluidName != null && ("xpjuice".equals(fluidName) || "liquid_xp".equals(fluidName) || "experience".equals(fluidName));
   }

   public static final boolean isLiquidXp(FluidStack supplied) {
      return supplied != null && supplied.getFluid() != null && isLiquidXp(supplied.getFluid().getName());
   }

   public static final float getFillLevel(World world, IBlockState state, @Nullable BlockPos pos) {
      Block block = state.getBlock();
      float filled = 1.0F;
      if (block instanceof IFluidBlock) {
         if (pos == null) {
            pos = BlockPos.ORIGIN;
         }

         filled = ((IFluidBlock)block).getFilledPercentage(world, pos);
      } else if (block instanceof BlockLiquid) {
         filled = BlockLiquid.getLiquidHeightPercent(block.getMetaFromState(state));
      }

      return filled;
   }

   public static final ItemStack getFilledBucket(ModIntegration modid, String bucketid, FluidStack of, int capacity) {
      ItemStack filled_bucket = modid.itemstack(bucketid);
      if (!filled_bucket.isEmpty()) {
         if (capacity <= 0) {
            capacity = 1000;
         }

         FluidStack contents = new FluidStack(of, capacity);
         NBTTagCompound nbt = new NBTTagCompound();
         contents.writeToNBT(nbt);
         filled_bucket.setTagCompound(nbt);
      }

      return filled_bucket;
   }

   public static final ItemStack getFilledBucket(ModIntegration modid, String bucketid, FluidStack of) {
      return getFilledBucket(modid, bucketid, of, of.amount);
   }

   private Fluids() {
   }

   public static final void init() {
   }

   @SideOnly(Side.CLIENT)
   public static final class StateMapper extends StateMapperBase implements ItemMeshDefinition {
      @Nonnull
      public final Fluid fluid;
      @Nonnull
      public final ModelResourceLocation location;

      public StateMapper(@Nonnull String modid, @Nonnull Fluid fluid, boolean generic) {
         this.fluid = fluid;
         this.location = new ModelResourceLocation(modid + (generic ? ":fluids" : ":"), generic ? fluid.getName() : null);
      }

      @Nonnull
      protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
         return this.location;
      }

      @Nonnull
      public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
         return this.location;
      }
   }
}
