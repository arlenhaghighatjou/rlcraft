package com.charles445.simpledifficulty.compat.shadow;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.Fluid;

public class InspirationsShadow {
   public InspirationsShadow() {
   }

   public interface ICauldronRecipe {
      boolean matches(ItemStack var1, boolean var2, int var3, InspirationsShadow.ICauldronRecipe.CauldronState var4);

      ItemStack getResult(ItemStack var1, boolean var2, int var3, InspirationsShadow.ICauldronRecipe.CauldronState var4);

      ItemStack getContainer(ItemStack var1);

      SoundEvent getSound(ItemStack var1, boolean var2, int var3, InspirationsShadow.ICauldronRecipe.CauldronState var4);

      ItemStack transformInput(ItemStack var1, boolean var2, int var3, InspirationsShadow.ICauldronRecipe.CauldronState var4);

      int getLevel(int var1);

      public static class CauldronState {
         public CauldronState() {
         }

         public Fluid getFluid() {
            return null;
         }
      }
   }
}
