package rustic.common.crafting;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.Config;
import rustic.common.blocks.fluids.FluidBooze;

public class BrewingBarrelRecipe implements IBrewingBarrelRecipe {
   private static final EaglercraftRandom rand = new EaglercraftRandom();
   protected final FluidStack input;
   protected final FluidStack output;

   public BrewingBarrelRecipe(FluidStack output, FluidStack input) {
      this.output = output;
      this.input = input;
   }

   public FluidStack getInput() {
      return this.input.copy();
   }

   public FluidStack getOuput() {
      return this.output.copy();
   }

   public boolean matches(FluidStack in) {
      return this.input != null && this.input.getFluid() != null && in != null && in.getFluid() != null ? in.getFluid().getName().equals(this.input.getFluid().getName()) : false;
   }

   public boolean matches(FluidStack in, FluidStack aux) {
      if (this.output != null && this.output.getFluid() != null && aux != null && aux.getFluid() != null) {
         return this.matches(in) && aux.getFluid() == this.output.getFluid();
      } else {
         return aux == null ? this.matches(in) : false;
      }
   }

   public FluidStack getResult(FluidStack in) {
      if (this.matches(in) && this.output != null && this.output.getFluid() != null) {
         FluidStack out = this.output.copy();
         if (this.output.getFluid() instanceof FluidBooze) {
            if (out.tag == null) {
               out.tag = new NBTTagCompound();
            }

            out.tag.setFloat("Quality", (float)(5 + rand.nextInt(71)) / 100.0F);
         }

         return out;
      } else {
         return null;
      }
   }

   public FluidStack getResult(FluidStack in, FluidStack aux) {
      if (aux != null && aux.getFluid() != null && this.matches(in, aux) && aux.getFluid() instanceof FluidBooze) {
         if (this.matches(in, aux) && this.output != null && this.output.getFluid() != null) {
            FluidStack out = this.output.copy();
            if (this.output.getFluid() instanceof FluidBooze && aux.tag != null && aux.tag.hasKey("Quality")) {
               float auxQuality = aux.tag.getFloat("Quality");
               if (Config.MAX_BREW_QUALITY_CHANGE < Config.MIN_BREW_QUALITY_CHANGE) {
                  Config.MAX_BREW_QUALITY_CHANGE = Config.MIN_BREW_QUALITY_CHANGE;
               }

               int brewQualityChange = rand.nextInt(Config.MAX_BREW_QUALITY_CHANGE - Config.MIN_BREW_QUALITY_CHANGE + 1) + Config.MIN_BREW_QUALITY_CHANGE;
               float quality = Math.max(Math.min((float)(brewQualityChange + (int)(100.0F * auxQuality)) / 100.0F, 1.0F), 0.0F);
               if (out.tag == null) {
                  out.tag = new NBTTagCompound();
               }

               out.tag.setFloat("Quality", quality);
            }

            return out;
         } else {
            return null;
         }
      } else {
         return this.getResult(in);
      }
   }
}
