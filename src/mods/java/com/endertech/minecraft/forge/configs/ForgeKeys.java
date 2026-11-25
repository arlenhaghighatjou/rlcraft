package com.endertech.minecraft.forge.configs;

import com.endertech.common.Args;
import com.endertech.common.CommonString;
import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.units.UnitId;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ForgeKeys {
   protected static final String KEY = "key";
   protected static final String CATEGORIES = "categories";
   protected static final String DELIMITER = ".";

   public ForgeKeys() {
   }

   public static boolean affectPlayer() {
      return Minecraft.getMinecraft().inGameHasFocus;
   }

   public static boolean affectMenus() {
      return !affectPlayer();
   }

   public static GameSettings getGameSettings() {
      return Minecraft.getMinecraft().gameSettings;
   }

   public static boolean isDown(KeyBinding key) {
      return key != null ? GameSettings.isKeyDown(key) : false;
   }

   public static boolean isPressed(KeyBinding key) {
      return key != null ? key.isPressed() : false;
   }

   public static String getName(KeyBinding key) {
      String name = key != null ? key.getDisplayName() : "";
      return name.replace("Button 1", "Left Button").replace("Button 2", "Right Button").replace("Button 3", "Middle Button");
   }

   /** @deprecated */
   @Deprecated
   public static KeyBinding createBindingFor(String actionName, ForgeConfig config, String configCategory, int keyCode, ForgeKeys.KeyCategories keyCategory) {
      keyCode = ForgeConfig.getInt(config, configCategory, "key" + CommonString.capitalizeFirstChar(actionName), keyCode, ForgeBounds.KEY_CODE.getIntBounds(), "Defines the key code for the " + actionName + " action.\nKey codes can be found at: http://minecraft.gamepedia.com/Key_codes");
      ForgeConfig.save(config);
      return createBindingFor(actionName, keyCode, keyCategory);
   }

   public static KeyBinding createBindingFor(String actionName, int keyCode, ForgeKeys.KeyCategories keyCategory) {
      KeyBinding keyBinding = new KeyBinding(Args.joinDelim(".", "key", UnitId.dictToRegName(actionName)), keyCode, keyCategory.getName());
      ClientRegistry.registerKeyBinding(keyBinding);
      return keyBinding;
   }

   public static enum KeyCategories implements IForgeEnum {
      NONE,
      MOVEMENT,
      GAMEPLAY,
      INVENTORY,
      CREATIVE,
      MULTIPLAYER,
      UI,
      MISC;

      private KeyCategories() {
      }

      public String getName() {
         return Args.joinDelim(".", "key", "categories", this.getLowerCase(this.name()));
      }
   }
}
