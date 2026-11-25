package net.silentchaos512.lib.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class ChatHelper {
   public ChatHelper() {
   }

   public static void sendMessage(EntityPlayer player, String message) {
      player.sendMessage(new TextComponentString(message));
   }

   public static void sendMessage(EntityPlayer player, ITextComponent component) {
      player.sendMessage(component);
   }

   public static void sendStatusMessage(EntityPlayer player, String message, boolean actionBar) {
      player.sendStatusMessage(new TextComponentString(message), actionBar);
   }

   public static void sendStatusMessage(EntityPlayer player, ITextComponent component, boolean actionBar) {
      player.sendStatusMessage(component, actionBar);
   }

   public static void translate(EntityPlayer player, String translationKey) {
      sendMessage(player, new TextComponentTranslation(translationKey, new Object[0]));
   }

   public static void translate(EntityPlayer player, String translationKey, Object... args) {
      sendMessage(player, new TextComponentTranslation(translationKey, args));
   }

   public static void translateStatus(EntityPlayer player, String translationKey, boolean actionBar) {
      sendStatusMessage(player, new TextComponentTranslation(translationKey, new Object[0]), actionBar);
   }

   public static void translateStatus(EntityPlayer player, String translationKey, boolean actionBar, Object... args) {
      sendStatusMessage(player, new TextComponentTranslation(translationKey, args), actionBar);
   }
}
