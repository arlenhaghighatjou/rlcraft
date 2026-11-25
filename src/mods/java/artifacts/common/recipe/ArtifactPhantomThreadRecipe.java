package artifacts.common.recipe;

import artifacts.common.item.BaubleBase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import wiresegal.classyhats.ClassyHatsContent;

public class ArtifactPhantomThreadRecipe extends Impl<IRecipe> implements IRecipe {
   public ArtifactPhantomThreadRecipe() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return this.validInput(inv) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      if (slots == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack stack = inv.getStackInSlot(slots[0].intValue()).copy();
         if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
         }

         stack.getTagCompound().setBoolean("phantom_thread_invisible", !stack.getTagCompound().getBoolean("phantom_thread_invisible"));
         return stack;
      }
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   @Nullable
   private Integer[] validInput(InventoryCrafting inv) {
      int numStacks = 0;
      int artifactSlot = -1;
      int threadSlot = -1;
      List<Integer> occupiedSlots = new ArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         if (!inv.getStackInSlot(i).isEmpty()) {
            ++numStacks;
            occupiedSlots.add(Integer.valueOf(i));
         }
      }

      if (numStacks != 2) {
         return null;
      } else {
         Iterator var9 = occupiedSlots.iterator();

         while(var9.hasNext()) {
            int i = ((Integer)var9.next()).intValue();
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack.getItem() instanceof BaubleBase) {
               artifactSlot = i;
            } else {
               if (!itemStack.getItem().equals(ClassyHatsContent.INSTANCE.getPHANTOM_THREAD())) {
                  return null;
               }

               threadSlot = i;
            }
         }

         Integer[] slots = new Integer[2];
         slots[0] = artifactSlot;
         slots[1] = threadSlot;
         return artifactSlot != -1 && threadSlot != -1 ? slots : null;
      }
   }
}
