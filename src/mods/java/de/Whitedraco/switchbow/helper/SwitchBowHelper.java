package de.Whitedraco.switchbow.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SwitchBowHelper {
   private static final String TAG_BOW_SELECTIONARROW = "tag_bowinfo_selectionArrow";

   public SwitchBowHelper() {
   }

   public static ItemStack getSelectionArrow(ItemStack bow) {
      if (bow.getTagCompound() != null) {
         NBTTagCompound data = bow.getTagCompound().getCompoundTag("tag_bowinfo_selectionArrow");
         if (data != null) {
            return new ItemStack(data);
         }
      }

      return ItemStack.EMPTY;
   }

   public static void setSelectionArrow(ItemStack bow, ItemStack selectionArrow) {
      NBTTagCompound data = new NBTTagCompound();
      if (selectionArrow != ItemStack.EMPTY) {
         selectionArrow.writeToNBT(data);
      }

      bow.setTagInfo("tag_bowinfo_selectionArrow", data);
   }
}
