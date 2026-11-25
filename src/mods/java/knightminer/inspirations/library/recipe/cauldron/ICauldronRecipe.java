package knightminer.inspirations.library.recipe.cauldron;

import javax.annotation.Nonnull;
import knightminer.inspirations.library.InspirationsRegistry;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public interface ICauldronRecipe {
   boolean matches(ItemStack var1, boolean var2, int var3, ICauldronRecipe.CauldronState var4);

   default ItemStack getResult(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return ItemStack.EMPTY;
   }

   default ItemStack transformInput(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      stack.shrink(1);
      return stack;
   }

   default int getLevel(int level) {
      return level;
   }

   default ICauldronRecipe.CauldronState getState(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return state;
   }

   default SoundEvent getSound(ItemStack stack, boolean boiling, int level, ICauldronRecipe.CauldronState state) {
      return SoundEvents.ENTITY_BOBBER_SPLASH;
   }

   default float getVolume(SoundEvent sound) {
      return sound == SoundEvents.ENTITY_BOBBER_SPLASH ? 0.3F : 1.0F;
   }

   default ItemStack getContainer(ItemStack stack) {
      return stack.getItem().getContainerItem(stack).copy();
   }

   public static class CauldronState {
      private int color = -1;
      private PotionType potion = null;
      private FluidStack fluid = null;
      public static final ICauldronRecipe.CauldronState WATER = new ICauldronRecipe.CauldronState();
      public static final String TAG_WATER = "water";
      public static final String TAG_COLOR = "color";
      public static final String TAG_POTION = "potion";
      public static final String TAG_FLUID = "fluid";

      private CauldronState() {
      }

      public static ICauldronRecipe.CauldronState dye(int color) {
         if (color == -1) {
            return WATER;
         } else {
            ICauldronRecipe.CauldronState state = new ICauldronRecipe.CauldronState();
            state.color = color;
            return state;
         }
      }

      public static ICauldronRecipe.CauldronState potion(PotionType potion) {
         if (potion == PotionTypes.WATER) {
            return WATER;
         } else {
            ICauldronRecipe.CauldronState state = new ICauldronRecipe.CauldronState();
            state.potion = potion;
            return state;
         }
      }

      public static ICauldronRecipe.CauldronState fluid(Fluid fluid) {
         if (fluid == FluidRegistry.WATER) {
            return WATER;
         } else {
            ICauldronRecipe.CauldronState state = new ICauldronRecipe.CauldronState();
            state.fluid = new FluidStack(fluid, 1000);
            return state;
         }
      }

      public boolean isWater() {
         return this == WATER || this.fluid != null && InspirationsRegistry.isCauldronWater(this.fluid.getFluid());
      }

      public int getColor() {
         return this.color;
      }

      public PotionType getPotion() {
         return this == WATER ? PotionTypes.WATER : this.potion;
      }

      public Fluid getFluid() {
         if (this == WATER) {
            return FluidRegistry.WATER;
         } else {
            return this.fluid == null ? null : this.fluid.getFluid();
         }
      }

      public FluidStack getFluidStack() {
         if (this == WATER) {
            return new FluidStack(FluidRegistry.WATER, 1000);
         } else {
            return this.fluid == null ? null : this.fluid.copy();
         }
      }

      public boolean matches(ICauldronRecipe.CauldronState state) {
         return this == state || state.color == this.color && state.potion == this.potion && state.getFluid() == this.getFluid();
      }

      public static boolean fluidValid(FluidStack fluid) {
         return fluid != null && fluid.amount == 1000 && fluid.tag == null;
      }

      public static ICauldronRecipe.CauldronState fromNBT(NBTTagCompound tags) {
         if (tags.getBoolean("water")) {
            return WATER;
         } else {
            ICauldronRecipe.CauldronState state = new ICauldronRecipe.CauldronState();
            if (tags.hasKey("color")) {
               state.color = tags.getInteger("color");
            }

            if (tags.hasKey("potion")) {
               state.potion = PotionType.getPotionTypeForName(tags.getString("potion"));
            }

            if (tags.hasKey("fluid")) {
               Fluid fluid = FluidRegistry.getFluid(tags.getString("fluid"));
               if (fluid != null) {
                  state.fluid = new FluidStack(fluid, 1000);
               }
            }

            return state;
         }
      }

      @Nonnull
      public NBTTagCompound writeToNBT() {
         NBTTagCompound tags = new NBTTagCompound();
         if (this == WATER) {
            tags.setBoolean("water", true);
            return tags;
         } else {
            if (this.color > -1) {
               tags.setInteger("color", this.color);
            }

            if (this.potion != null) {
               tags.setString("potion", this.potion.getRegistryName().toString());
            }

            if (this.fluid != null) {
               tags.setString("fluid", this.fluid.getFluid().getName());
            }

            return tags;
         }
      }

      public boolean equals(Object other) {
         if (this == other) {
            return true;
         } else {
            return other != null && this.getClass() == other.getClass() ? this.matches((ICauldronRecipe.CauldronState)other) : false;
         }
      }

      public int hashCode() {
         int hashCode = 0;
         if (this.color > -1) {
            hashCode = this.color;
         }

         if (this.potion != null) {
            hashCode = hashCode * 31 + this.potion.hashCode();
         }

         if (this.fluid != null) {
            hashCode = hashCode * 31 + this.fluid.getFluid().getName().hashCode();
         }

         return hashCode;
      }
   }
}
