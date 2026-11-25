package moe.plushie.armourers_workshop.api.common.painting;

import net.minecraft.item.ItemStack;

public interface IPaintingTool {
   /** @deprecated */
   @Deprecated
   boolean getToolHasColour(ItemStack var1);

   int getToolColour(ItemStack var1);

   void setToolColour(ItemStack var1, int var2);

   void setToolPaintType(ItemStack var1, IPaintType var2);

   IPaintType getToolPaintType(ItemStack var1);
}
