package net.silentchaos512.lib.item;

import java.awt.Color;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.silentchaos512.lib.event.ClientTicks;

public interface ICustomEnchantColor {
   String NBT_LIB_EFFECT_COLOR = "SilentLib:EffectColor";
   String NBT_QUARK_RUNE_ATTACHED = "Quark:RuneAttached";
   String NBT_QUARK_RUNE_COLOR = "Quark:RuneColor";

   default int getEffectColor(ItemStack stack) {
      NBTTagCompound tagCompound = stack.getTagCompound();
      if (tagCompound != null) {
         if (tagCompound.hasKey("SilentLib:EffectColor")) {
            return tagCompound.getInteger("SilentLib:EffectColor");
         }

         if (tagCompound.hasKey("Quark:RuneAttached") && tagCompound.hasKey("Quark:RuneColor")) {
            int value = tagCompound.getInteger("Quark:RuneColor");
            if (value > 15) {
               return Color.HSBtoRGB(ClientTicks.totalTicks * 0.005F, 1.0F, 0.6F);
            }

            if (value >= 0) {
               return EnumDyeColor.byMetadata(value).getColorValue();
            }
         }
      }

      return 16777215;
   }

   default boolean shouldTruncateBrightness(ItemStack stack) {
      return true;
   }

   default int getEffectMaxBrightness(ItemStack stack) {
      return 396;
   }
}
