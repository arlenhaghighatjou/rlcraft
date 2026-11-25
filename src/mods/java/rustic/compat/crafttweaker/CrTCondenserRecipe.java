package rustic.compat.crafttweaker;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.ICondenserRecipe;

public class CrTCondenserRecipe implements ICondenserRecipe {
   private int time;
   private ItemStack output;
   private FluidStack fluid;
   private IIngredient bottle;
   private IIngredient modifier;
   private List<IIngredient> inputs;

   public CrTCondenserRecipe(@Nonnull ItemStack output, IIngredient[] inputs) {
      this(output, inputs, (IIngredient)null);
   }

   public CrTCondenserRecipe(@Nonnull ItemStack output, IIngredient[] inputs, IIngredient modifier) {
      this(output, inputs, modifier, CraftTweakerMC.getIItemStack(new ItemStack(Items.GLASS_BOTTLE)));
   }

   public CrTCondenserRecipe(@Nonnull ItemStack output, IIngredient[] inputs, IIngredient modifier, IIngredient bottle) {
      this(output, inputs, modifier, bottle, new FluidStack(FluidRegistry.WATER, 125));
   }

   public CrTCondenserRecipe(@Nonnull ItemStack output, IIngredient[] inputs, IIngredient modifier, IIngredient bottle, @Nonnull FluidStack fluid) {
      this(output, inputs, modifier, bottle, fluid, 400);
   }

   public CrTCondenserRecipe(@Nonnull ItemStack output, IIngredient[] inputs, IIngredient modifier, IIngredient bottle, @Nonnull FluidStack fluid, int time) {
      this.output = output;
      this.fluid = fluid;
      this.bottle = bottle;
      this.modifier = modifier;
      this.inputs = new ArrayList();

      for(IIngredient ing : inputs) {
         this.inputs.add(ing);
      }

      this.time = Math.max(0, time);
   }

   public boolean matches(Fluid fluid, ItemStack modifier, ItemStack bottle, ItemStack[] inputs) {
      if (fluid != this.fluid.getFluid()) {
         return false;
      } else if ((this.modifier == null || this.modifier.matchesExact(CraftTweakerMC.getIItemStack(modifier))) && (this.modifier != null || modifier.isEmpty())) {
         if (this.bottle != null && !this.bottle.matchesExact(CraftTweakerMC.getIItemStack(bottle))) {
            return false;
         } else {
            List<IIngredient> tempInputs = new ArrayList(this.inputs);

            for(ItemStack stack : inputs) {
               if (stack != null && !stack.isEmpty()) {
                  boolean stackNotInput = true;
                  IItemStack input = CraftTweakerMC.getIItemStack(stack);

                  for(IIngredient in : tempInputs) {
                     if (in == null) {
                        tempInputs.remove(in);
                     } else if (in.matchesExact(input)) {
                        stackNotInput = false;
                        tempInputs.remove(in);
                        break;
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

   public boolean isBasic() {
      return this.modifier == null && this.inputs.size() <= 2;
   }

   public boolean isAdvanced() {
      return this.inputs.size() <= 3;
   }

   public FluidStack getFluid() {
      return this.fluid;
   }

   public List<ItemStack> getModifiers() {
      return (List<ItemStack>)(this.modifier != null ? (List)this.modifier.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList()) : new ArrayList());
   }

   public List<ItemStack> getBottles() {
      return this.bottle == null ? Collections.singletonList(ItemStack.EMPTY) : (List)this.bottle.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
   }

   public List<List<ItemStack>> getInputs() {
      List<List<ItemStack>> inputs = new ArrayList();

      for(IIngredient input : this.inputs) {
         if (input == null) {
            inputs.add(new ArrayList());
         } else {
            inputs.add(input.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList()));
         }
      }

      return inputs;
   }

   public int getTime() {
      return this.time;
   }

   public int getModifierConsumption(ItemStack modifier) {
      if (this.modifier != null) {
         IItemStack mod = CraftTweakerMC.getIItemStack(modifier);

         for(IItemStack stack : this.modifier.getItems()) {
            if (stack.matchesExact(mod)) {
               return stack.getAmount();
            }
         }
      }

      return 0;
   }

   public int getBottleConsumption(ItemStack bottle) {
      if (this.bottle == null) {
         return 0;
      } else {
         IItemStack btl = CraftTweakerMC.getIItemStack(bottle);

         for(IItemStack stack : this.bottle.getItems()) {
            if (stack.matchesExact(btl)) {
               return stack.getAmount();
            }
         }

         return 1;
      }
   }

   public int[] getInputConsumption(ItemStack[] inputs) {
      int[] consume = new int[inputs.length];
      List<IIngredient> tempInputs = new ArrayList(this.inputs);

      for(int i = 0; i < inputs.length; ++i) {
         consume[i] = 0;
         if (inputs[i] != null) {
            IItemStack current = CraftTweakerMC.getIItemStack(inputs[i]);

            label30:
            for(IIngredient in : tempInputs) {
               Iterator var8 = in.getItems().iterator();

               IItemStack stack;
               while(true) {
                  if (!var8.hasNext()) {
                     continue label30;
                  }

                  stack = (IItemStack)var8.next();
                  if (stack.matchesExact(current)) {
                     break;
                  }
               }

               consume[i] = stack.getAmount();
               break;
            }
         }
      }

      return consume;
   }

   public ItemStack getResult() {
      return this.output.copy();
   }
}
