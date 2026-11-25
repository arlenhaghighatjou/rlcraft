package rustic.common.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public abstract class CondenserRecipe implements ICondenserRecipe {
   ItemStack output;
   List<ItemStack> inputs;

   public CondenserRecipe() {
   }

   public boolean matches(Fluid fluid, ItemStack modifier, ItemStack bottle, ItemStack[] inputs) {
      if (inputs.length <= 0) {
         return false;
      } else if (this.inputs != null && this.inputs.size() > 0) {
         if (bottle.getItem() != Items.GLASS_BOTTLE) {
            return false;
         } else if (fluid != FluidRegistry.WATER) {
            return false;
         } else {
            List<ItemStack> tempInputs = new ArrayList(this.inputs);

            for(int i = 0; i < inputs.length; ++i) {
               ItemStack stack = inputs[i].copy();
               if (!stack.isEmpty()) {
                  boolean stackNotInput = true;

                  for(ItemStack temp : tempInputs) {
                     if (temp.getItem().equals(stack.getItem()) && temp.getMetadata() == stack.getMetadata()) {
                        if (temp.hasTagCompound() && stack.hasTagCompound()) {
                           if (temp.getTagCompound().equals(stack.getTagCompound())) {
                              tempInputs.remove(stack);
                              stackNotInput = false;
                              break;
                           }
                        } else if (!temp.hasTagCompound()) {
                           tempInputs.remove(temp);
                           stackNotInput = false;
                           break;
                        }
                     }
                  }

                  if (stackNotInput) {
                     return false;
                  }
               }
            }

            return tempInputs.size() == 0;
         }
      } else {
         return false;
      }
   }

   public FluidStack getFluid() {
      return new FluidStack(FluidRegistry.WATER, 125);
   }

   public List<ItemStack> getBottles() {
      return Arrays.asList(new ItemStack(Items.GLASS_BOTTLE));
   }

   public List<List<ItemStack>> getInputs() {
      List<List<ItemStack>> inputList = new ArrayList();

      for(ItemStack stack : this.inputs) {
         inputList.add(Arrays.asList(stack.copy()));
      }

      return inputList;
   }

   public int[] getInputConsumption(ItemStack[] inputs) {
      return new int[]{1, 1, 1};
   }

   public int getBottleConsumption(ItemStack bottle) {
      return 1;
   }

   public int getModifierConsumption(ItemStack modifier) {
      return 1;
   }

   public ItemStack getResult() {
      return this.output.copy();
   }
}
