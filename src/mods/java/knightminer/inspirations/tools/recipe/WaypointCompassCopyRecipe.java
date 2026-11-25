package knightminer.inspirations.tools.recipe;

import knightminer.inspirations.tools.InspirationsTools;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class WaypointCompassCopyRecipe extends Impl<IRecipe> implements IRecipe {
   private ItemStack OUTPUT = new ItemStack(InspirationsTools.waypointCompass);

   public WaypointCompassCopyRecipe() {
   }

   public boolean matches(InventoryCrafting inv, World world) {
      boolean foundWaypoint = false;
      ItemStack blank = ItemStack.EMPTY;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (!stack.isEmpty()) {
            if (!ItemWaypointCompass.isWaypointCompass(stack)) {
               return false;
            }

            if (ItemWaypointCompass.getDimension(stack) != null) {
               if (foundWaypoint) {
                  return false;
               }

               foundWaypoint = true;
            } else {
               if (!blank.isEmpty() && blank.getMetadata() != stack.getMetadata()) {
                  return false;
               }

               blank = stack;
            }
         }
      }

      return foundWaypoint && !blank.isEmpty();
   }

   public ItemStack getRecipeOutput() {
      return this.OUTPUT;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack waypoint = ItemStack.EMPTY;
      ItemStack result = ItemStack.EMPTY;
      int count = 0;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (ItemWaypointCompass.isWaypointCompass(stack)) {
            if (ItemWaypointCompass.getDimension(stack) != null) {
               waypoint = stack;
            } else {
               result = stack;
               ++count;
            }
         }
      }

      if (!result.isEmpty() && !waypoint.isEmpty()) {
         if (result.getItem() == Items.COMPASS) {
            result = new ItemStack(InspirationsTools.waypointCompass, 1, 0);
         } else {
            result = result.copy();
         }

         result.setCount(count);
         ItemWaypointCompass.copyNBT(result, waypoint);
         return result;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      NonNullList<ItemStack> items = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack.getItem() == InspirationsTools.waypointCompass && ItemWaypointCompass.getDimension(stack) != null) {
            stack = stack.copy();
            stack.setCount(1);
            items.set(i, stack);
            break;
         }
      }

      return items;
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public boolean isDynamic() {
      return true;
   }
}
