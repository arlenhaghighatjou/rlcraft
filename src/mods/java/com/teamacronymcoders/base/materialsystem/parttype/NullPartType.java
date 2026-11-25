package com.teamacronymcoders.base.materialsystem.parttype;

import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import net.minecraft.item.ItemStack;

public class NullPartType extends PartType {
   public NullPartType() {
      super("NULL");
   }

   public ItemStack getItemStack(MaterialPart materialPart) {
      return ItemStack.EMPTY;
   }
}
