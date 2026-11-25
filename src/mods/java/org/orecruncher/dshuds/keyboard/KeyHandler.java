package org.orecruncher.dshuds.keyboard;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dshuds.Environment;
import org.orecruncher.dshuds.ModOptions;
import org.orecruncher.dshuds.hud.LightLevelHUD;
import org.orecruncher.lib.Localization;
import org.orecruncher.lib.compat.EntityRendererUtil;

@SideOnly(Side.CLIENT)
public class KeyHandler {
   private static final String SECTION_NAME = "Dynamic Surroundings: HUDs";
   private static KeyBinding SELECTIONBOX_KEY;
   private static KeyBinding LIGHTLEVEL_KEY;
   private static KeyBinding CHUNKBORDER_KEY;
   private static final String chatPrefix = TextFormatting.BLUE + "[" + TextFormatting.GREEN + "Dynamic Surroundings: HUDs" + TextFormatting.BLUE + "] " + TextFormatting.RESET;

   public KeyHandler() {
   }

   public static void init() {
      SELECTIONBOX_KEY = new KeyBinding("dshuds.keybind.SelectionBox", 48, "Dynamic Surroundings: HUDs");
      ClientRegistry.registerKeyBinding(SELECTIONBOX_KEY);
      LIGHTLEVEL_KEY = new KeyBinding("dshuds.keybind.LightLevel", 65, "Dynamic Surroundings: HUDs");
      ClientRegistry.registerKeyBinding(LIGHTLEVEL_KEY);
      CHUNKBORDER_KEY = new KeyBinding("dshuds.keybind.ChunkBorders", 67, "Dynamic Surroundings: HUDs");
      ClientRegistry.registerKeyBinding(CHUNKBORDER_KEY);
      MinecraftForge.EVENT_BUS.register(KeyHandler.class);
   }

   private static String getOnOff(boolean flag) {
      return Localization.format(flag ? "dshuds.keybind.msg.ON" : "dshuds.keybind.msg.OFF", new Object[0]);
   }

   private static void sendPlayerMessage(String fmt, Object... parms) {
      if (!ModOptions.logging.hideChatNotices) {
         EntityPlayer player = Environment.getPlayer();
         if (player != null) {
            String txt = chatPrefix + Localization.format(fmt, parms);
            player.sendMessage(new TextComponentString(txt));
         }

      }
   }

   private static boolean shouldHandle(@Nonnull KeyBinding binding) {
      return binding != null && binding.isPressed();
   }

   @SubscribeEvent(
      receiveCanceled = false
   )
   public static void onKeyboard(@Nonnull KeyInputEvent event) {
      if (shouldHandle(SELECTIONBOX_KEY)) {
         EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;
         boolean result = !EntityRendererUtil.getDrawBlockOutline(renderer);
         EntityRendererUtil.setDrawBlockOutline(renderer, result);
         sendPlayerMessage("dshuds.keybind.msg.Fencing", getOnOff(result));
      }

      if (shouldHandle(CHUNKBORDER_KEY)) {
         boolean result = Minecraft.getMinecraft().debugRenderer.toggleChunkBorders();
         sendPlayerMessage("dshuds.keybind.msg.ChunkBorder", getOnOff(result));
      }

      if (shouldHandle(LIGHTLEVEL_KEY)) {
         if (GuiScreen.isCtrlKeyDown()) {
            if (LightLevelHUD.showHUD) {
               ++ModOptions.lightLevelHUD.displayMode;
               if (ModOptions.lightLevelHUD.displayMode >= LightLevelHUD.Mode.values().length) {
                  ModOptions.lightLevelHUD.displayMode = 0;
               }

               sendPlayerMessage("dshuds.keybind.msg.LLDisplayMode", LightLevelHUD.Mode.getMode(ModOptions.lightLevelHUD.displayMode).name());
            }
         } else if (GuiScreen.isShiftKeyDown()) {
            if (LightLevelHUD.showHUD) {
               ModOptions.lightLevelHUD.hideSafe = !ModOptions.lightLevelHUD.hideSafe;
               sendPlayerMessage("dshuds.keybind.msg.LLSafeBlocks", getOnOff(ModOptions.lightLevelHUD.hideSafe));
            }
         } else {
            LightLevelHUD.showHUD = !LightLevelHUD.showHUD;
            sendPlayerMessage("dshuds.keybind.msg.LLDisplay", getOnOff(LightLevelHUD.showHUD));
         }
      }

   }
}
