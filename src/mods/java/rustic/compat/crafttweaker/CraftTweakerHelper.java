package rustic.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CraftTweakerHelper {
   public CraftTweakerHelper() {
   }

   public static void preInit() {
      CraftTweakerAPI.registerClass(CrushingTub.class);
      CraftTweakerAPI.registerClass(EvaporatingBasin.class);
      CraftTweakerAPI.registerClass(Condenser.class);
   }

   public static ItemStack toStack(IItemStack iStack) {
      return iStack == null ? ItemStack.EMPTY : (ItemStack)iStack.getInternal();
   }

   public static ItemStack[] toStacks(IItemStack[] iStacks) {
      if (iStacks == null) {
         return new ItemStack[0];
      } else {
         ItemStack[] ret = new ItemStack[iStacks.length];

         for(int i = 0; i < iStacks.length; ++i) {
            ret[i] = toStack(iStacks[i]);
         }

         return ret;
      }
   }

   public static Object toObject(IIngredient iStack) {
      if (iStack == null) {
         return null;
      } else if (iStack instanceof IOreDictEntry) {
         return ((IOreDictEntry)iStack).getName();
      } else if (iStack instanceof IItemStack) {
         return toStack((IItemStack)iStack);
      } else if (iStack instanceof IngredientStack) {
         IIngredient ingr = (IIngredient)ReflectionHelper.getPrivateValue(IngredientStack.class, (IngredientStack)iStack, new String[]{"ingredient"});
         return toObject(ingr);
      } else {
         return null;
      }
   }

   public static Object[] toObjects(IIngredient[] iStacks) {
      Object[] oA = new Object[iStacks.length];

      for(int i = 0; i < iStacks.length; ++i) {
         oA[i] = toObject(iStacks[i]);
      }

      return oA;
   }

   public static FluidStack toFluidStack(ILiquidStack iStack) {
      return iStack == null ? null : (FluidStack)iStack.getInternal();
   }
}
