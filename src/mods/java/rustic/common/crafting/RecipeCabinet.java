package rustic.common.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import rustic.common.blocks.ModBlocks;

public class RecipeCabinet extends Impl<IRecipe> implements IRecipe {
   public RecipeCabinet() {
   }

   protected boolean hasOre(ItemStack stack, int ore) {
      if (stack.isEmpty()) {
         return false;
      } else {
         int[] oreIds = OreDictionary.getOreIDs(stack);

         for(int oreId : oreIds) {
            if (oreId == ore) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isTrapdoor(ItemStack stack) {
      Item item = stack.getItem();
      if (item == Item.getItemFromBlock(Blocks.TRAPDOOR)) {
         return true;
      } else {
         int trapdoorWood = OreDictionary.getOreID("trapdoorWood");
         return this.hasOre(stack, trapdoorWood);
      }
   }

   protected boolean isWood(ItemStack stack) {
      if (this.hasOre(stack, OreDictionary.getOreID("plankWood"))) {
         return true;
      } else if (stack.getItem().getRegistryName().toString().equals("embers:sealed_planks")) {
         return true;
      } else if (stack.getItem().getRegistryName().toString().equals("botania:livingwood") && stack.getMetadata() == 1) {
         return true;
      } else if (stack.getItem().getRegistryName().toString().equals("botania:dreamwood") && stack.getMetadata() == 1) {
         return true;
      } else if (stack.getItem().getRegistryName().toString().equals("astralsorcery:blockinfusedwood") && stack.getMetadata() == 1) {
         return true;
      } else {
         return this.hasOre(stack, OreDictionary.getOreID("plankTreatedWood"));
      }
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      ItemStack stack = ItemStack.EMPTY;

      for(int y = 0; y < 3; y += 2) {
         for(int x = 0; x < 3; ++x) {
            stack = inv.getStackInRowAndColumn(x, y);
            if (!this.isWood(stack)) {
               return false;
            }
         }
      }

      stack = inv.getStackInRowAndColumn(1, 1);
      if (!stack.isEmpty()) {
         return false;
      } else {
         ItemStack stack2 = inv.getStackInRowAndColumn(2, 1);
         stack = inv.getStackInRowAndColumn(0, 1);
         return this.isWood(stack) && this.isTrapdoor(stack2) || this.isWood(stack2) && this.isTrapdoor(stack);
      }
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack result = this.getRecipeOutput();
      ItemStack material = ItemStack.EMPTY;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i).copy();
         stack.setCount(1);
         if (this.isWood(stack)) {
            if (!material.isEmpty() && !ItemStack.areItemStacksEqual(material, stack)) {
               material = ItemStack.EMPTY;
               break;
            }

            material = stack;
         }
      }

      NBTTagCompound tag = new NBTTagCompound();
      tag.setTag("material", material.serializeNBT());
      result.setTagCompound(tag);
      return result;
   }

   public ItemStack getRecipeOutput() {
      return new ItemStack(ModBlocks.CABINET);
   }

   public boolean canFit(int width, int height) {
      return width >= 3 && height >= 3;
   }

   public boolean isDynamic() {
      return true;
   }
}
