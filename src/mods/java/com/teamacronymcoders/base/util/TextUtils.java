package com.teamacronymcoders.base.util;

import java.util.Locale;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class TextUtils {
   private TextUtils() {
   }

   public static String removeSpecialCharacters(String input) {
      return input.replace(" ", "").replace("'", "").replace(":", "_");
   }

   public static String toSnakeCase(String input) {
      input = removeSpecialCharacters(input);
      input = input.substring(0, 1).toLowerCase(Locale.US) + input.substring(1);
      StringBuilder output = new StringBuilder();

      for(int i = 0; i < input.length(); ++i) {
         char currentChar = input.charAt(i);
         if (Character.isUpperCase(currentChar)) {
            output.append("_");
         }

         output.append(Character.toLowerCase(currentChar));
      }

      return output.toString();
   }

   public static String getRegistryLocation(IForgeRegistryEntry entry) {
      ResourceLocation resourceLocation = entry.getRegistryName();
      String rl = "";
      if (resourceLocation != null) {
         rl = resourceLocation.toString();
      }

      return rl;
   }

   public static TextComponentBase representFluidStack(FluidStack stack) {
      return (TextComponentBase)(stack != null ? new TextComponentString(stack.getLocalizedName() + ": " + stack.amount + "mB") : new TextComponentTranslation("base.info.nofluid", new Object[0]));
   }

   public static TextComponentBase representTankContents(IFluidTank tank) {
      return (TextComponentBase)(tank.getFluid() != null && tank.getFluidAmount() > 0 ? new TextComponentString(tank.getFluid().getLocalizedName() + ": " + tank.getFluidAmount() + "mB/" + tank.getCapacity() + "mB") : new TextComponentTranslation("base.info.empty", new Object[0]));
   }

   public static TextComponentBase representInventoryContents(ItemStackHandler handler) {
      TextComponentTranslation start = new TextComponentTranslation("Inventory: ", new Object[0]);

      for(int i = 0; i < handler.getSlots(); ++i) {
         ItemStack current = handler.getStackInSlot(i);
         if (current != ItemStack.EMPTY) {
            start.appendText(current.getCount() + " " + current.getDisplayName() + " ");
         }
      }

      return start;
   }
}
