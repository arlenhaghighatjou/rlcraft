package com.endertech.minecraft.forge;

import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.IHaveConfig;
import com.endertech.minecraft.forge.regs.INeedToRegister;

public abstract class ForgeRecipe implements IHaveConfig, INeedToRegister {
   protected String statusDescription = "";

   public ForgeRecipe() {
   }

   public static String getStatus(ForgeRecipe recipe) {
      String status = "";
      if (recipe != null) {
         if (recipe.isValid()) {
            if (recipe.isReadyToRegister()) {
               status = "OK";
            } else {
               status = "NOT READY";
            }
         } else {
            status = "INVALID";
         }

         return status + "<" + recipe.statusDescription + ">";
      } else {
         return "recipe is NULL!";
      }
   }

   public abstract boolean isValid();

   public static enum Type implements IForgeEnum {
      MAIN,
      RESERVE;

      private Type() {
      }

      public String getName() {
         return this.getLowerCase(this.name());
      }
   }
}
