package rustic.common.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import rustic.common.Config;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.items.ModItems;

public class RecipeOliveOil extends Impl<IRecipe> implements IRecipe {
   private int foodSlot = 0;

   public RecipeOliveOil() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      int numStacks = 0;
      List<Integer> occupiedSlots = new ArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         if (!inv.getStackInSlot(i).isEmpty()) {
            ++numStacks;
            occupiedSlots.add(Integer.valueOf(i));
         }
      }

      if (numStacks != 2) {
         return false;
      } else {
         ItemStack foodStack = ItemStack.EMPTY;
         ItemStack oilStack = ItemStack.EMPTY;
         Iterator var7 = occupiedSlots.iterator();

         while(var7.hasNext()) {
            int i = ((Integer)var7.next()).intValue();
            ItemStack tempStack = inv.getStackInSlot(i);
            if (!(tempStack.getItem() instanceof ItemFood) || !foodStack.isEmpty()) {
               if (!tempStack.getItem().equals(ModItems.FLUID_BOTTLE) || !oilStack.isEmpty()) {
                  return false;
               }

               FluidStack fluidStack = FluidUtil.getFluidContained(tempStack);
               if (fluidStack != null && fluidStack.getFluid() != null && fluidStack.getFluid().equals(ModFluids.OLIVE_OIL)) {
                  oilStack = tempStack;
               }
            } else {
               String itemName = ((ResourceLocation)Objects.requireNonNull(tempStack.getItem().getRegistryName())).toString();
               if (Config.OLIVE_OIL_USE_WHITELIST != Config.OLIVE_OIL_BLACKLIST.contains(itemName)) {
                  return false;
               }

               foodStack = tempStack;
               this.foodSlot = i;
            }
         }

         if (foodStack.hasTagCompound() && foodStack.getTagCompound().hasKey("oiled")) {
            return false;
         } else {
            return true;
         }
      }
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack foodStack = inv.getStackInSlot(this.foodSlot);
      if (!foodStack.isEmpty() && foodStack.getItem() instanceof ItemFood) {
         ItemStack returnStack = new ItemStack(foodStack.getItem(), 1, foodStack.getItemDamage());
         NBTTagCompound amendedTag = foodStack.getTagCompound();
         if (amendedTag == null) {
            amendedTag = new NBTTagCompound();
         } else {
            amendedTag = amendedTag.copy();
         }

         amendedTag.setTag("oiled", new NBTTagByte((byte)0));
         returnStack.setTagCompound(amendedTag);
         return returnStack;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      NonNullList items = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
      return items;
   }

   public boolean canFit(int width, int height) {
      return width <= 3 && height <= 3;
   }

   public boolean isDynamic() {
      return true;
   }
}
