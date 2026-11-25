package com.elenai.elenaidodge.gui;

import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.util.ClientStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DodgeGui {
   public static ResourceLocation DODGE_ICONS = new ResourceLocation("elenaidodge", "textures/gui/icons.png");
   public static float alpha = 1.0F;

   public DodgeGui() {
   }

   @SubscribeEvent
   public void onRenderDodgeGUIEvent(Post event) {
      if (ModConfig.client.hud.hud && !Minecraft.getMinecraft().player.isCreative() && !Minecraft.getMinecraft().player.isSpectator() && !Minecraft.getMinecraft().isGamePaused() && event.getType() == ElementType.ALL) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(DODGE_ICONS);
         GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
         GlStateManager.tryBlendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
         enableAlpha(alpha);
         int i = Minecraft.getMinecraft().gameSettings.thirdPersonView > 0 ? event.getResolution().getScaledHeight() - 64 + 3 : event.getResolution().getScaledHeight() / 2 - 7 + 16;
         int j = event.getResolution().getScaledWidth() / 2 - 8 - 5;
         if (alpha > 0.0F && ClientStorage.dodges > 0) {
            if (ModConfig.client.hud.legacyHud) {
               int scaledHeight = event.getResolution().getScaledHeight() - 32 + 3;
               int scaledWidth = event.getResolution().getScaledWidth() / 2 - 91;
               enableAlpha(0.75F);
               gui.drawTexturedModalRect(scaledWidth, scaledHeight, 0, 16, (int)Math.ceil(ClientStorage.dodgesDouble * 6.0D), 5);
               disableAlpha(0.75F);
            } else {
               gui.drawTexturedModalRect(j, i, 16, 0, 27, 16);
               gui.drawTexturedModalRect(j, i, 43, 0, ClientStorage.dodges, 16);
            }
         }

         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         disableAlpha(alpha);
         Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
      }

   }

   public static void enableAlpha(float alpha) {
      GlStateManager.enableBlend();
      if (alpha != 1.0F) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
         GlStateManager.blendFunc(770, 771);
      }
   }

   public static void disableAlpha(float alpha) {
      GlStateManager.disableBlend();
      if (alpha != 1.0F) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
