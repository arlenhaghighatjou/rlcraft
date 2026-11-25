package net.mcft.copy.backpacks.misc.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class LangUtils {
   private LangUtils() {
   }

   @SideOnly(Side.CLIENT)
   public static List<String> format(String langKey, Object... args) {
      return formatPrepend("", langKey, args);
   }

   @SideOnly(Side.CLIENT)
   public static void format(List<String> list, String langKey, Object... args) {
      formatPrepend(list, "", langKey, args);
   }

   @SideOnly(Side.CLIENT)
   public static List<String> formatPrepend(String prepend, String langKey, Object... args) {
      ArrayList<String> list = new ArrayList();
      formatPrepend(list, prepend, langKey, args);
      return list;
   }

   @SideOnly(Side.CLIENT)
   public static void formatPrepend(List<String> list, String prepend, String langKey, Object... args) {
      String translated = I18n.format(langKey, args);

      for(String line : translated.split("\\\\n")) {
         list.add(prepend + line);
      }

   }

   @SideOnly(Side.CLIENT)
   public static void formatTooltip(List<String> tooltip, String tooltipKey, Object... args) {
      format(tooltip, "tooltip.wearablebackpacks." + tooltipKey, args);
   }

   @SideOnly(Side.CLIENT)
   public static void formatTooltipPrepend(List<String> tooltip, String prepend, String tooltipKey, Object... args) {
      formatPrepend(tooltip, prepend, "tooltip.wearablebackpacks." + tooltipKey, args);
   }

   @SideOnly(Side.CLIENT)
   public static void formatTooltipKey(List<String> tooltip, String langKey, KeyBinding keyBinding) {
      if (keyBinding.getKeyCode() != 0) {
         formatTooltip(tooltip, langKey, "§6" + keyBinding.getDisplayName() + "§7");
      }
   }

   @SideOnly(Side.CLIENT)
   public static boolean tooltipIsShiftKeyDown(List<String> tooltip) {
      boolean shift = GuiScreen.isShiftKeyDown();
      if (!shift) {
         formatTooltip(tooltip, "moreInfo", "§6SHIFT§7");
      }

      return shift;
   }

   public static void chatMessage(EntityPlayer player, String langKey, Object... args) {
      player.sendMessage(new TextComponentTranslation("chat.wearablebackpacks." + langKey, args));
   }

   @SideOnly(Side.CLIENT)
   public static void displayChatMessage(String langKey, Object... args) {
      chatMessage(ClientUtils.getPlayer(), langKey, args);
   }
}
