package com.charles445.simpledifficulty.client.gui;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDCompatibility;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.RenderUtil;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ThirstGui {
   private final Minecraft minecraftInstance = Minecraft.getMinecraft();
   private int updateCounter = 0;
   private final EaglercraftRandom rand = new EaglercraftRandom();
   public static final ResourceLocation ICONS = new ResourceLocation("simpledifficulty:textures/gui/icons.png");
   public static final ResourceLocation THIRSTHUD = new ResourceLocation("simpledifficulty:textures/gui/thirsthud.png");
   private static final int texturepos_X = 0;
   private static final int texturepos_Y = 0;
   private static final int textureWidth = 9;
   private static final int textureHeight = 9;

   public ThirstGui() {
   }

   public void register(EventBus bus) {
      bus.register(Pre.class, this::onPreRenderGameOverlay);
      bus.register(ClientTickEvent.class, this::onClientTick);
   }

   public void onPreRenderGameOverlay(Pre event) {
      if (event.getType() == ElementType.AIR && QuickConfig.isThirstEnabled() && SDCompatibility.defaultThirstDisplay) {
         this.rand.setSeed((long)(this.updateCounter * 445));
         boolean classic = ModConfig.client.classicHUDThirst;
         if (classic) {
            this.bind(ICONS);
         } else {
            this.bind(THIRSTHUD);
         }

         EntityPlayerSP player = Minecraft.getMinecraft().player;
         IThirstCapability capability = SDCapabilities.getThirstData(player);
         ScaledResolution resolution = event.getResolution();
         this.renderThirst(resolution.getScaledWidth(), resolution.getScaledHeight(), capability.getThirstLevel(), capability.getThirstSaturation());
         this.bind(Gui.ICONS);
         GuiIngameForge.right_height += 10;
      }

   }

   public void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END && !this.minecraftInstance.isGamePaused()) {
         ++this.updateCounter;
      }

   }

   private void renderThirst(int width, int height, int thirst, float thirstSaturation) {
      GlStateManager.enableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      int left = width / 2 + 82;
      int top = height - GuiIngameForge.right_height;

      for(int i = 0; i < 10; ++i) {
         int halfIcon = i * 2 + 1;
         int x = left - i * 8;
         int y = top;
         int bgXOffset = 0;
         int xOffset = 0;
         if (Minecraft.getMinecraft().player.isPotionActive(SDPotions.thirsty)) {
            xOffset += 36;
            bgXOffset = 117;
         }

         if (thirstSaturation <= 0.0F && this.updateCounter % (thirst * 3 + 1) == 0) {
            y = top + (this.rand.nextInt(3) - 1);
         }

         RenderUtil.drawTexturedModalRect((float)x, (float)y, 0 + bgXOffset, 0, 9, 9);
         if (halfIcon < thirst) {
            RenderUtil.drawTexturedModalRect((float)x, (float)y, 0 + xOffset + 36, 0, 9, 9);
         } else if (halfIcon == thirst) {
            RenderUtil.drawTexturedModalRect((float)x, (float)y, 0 + xOffset + 45, 0, 9, 9);
         }
      }

      int thirstSaturationInt = (int)thirstSaturation;
      if (thirstSaturationInt > 0 && ModConfig.client.drawThirstSaturation) {
         for(int i = 0; i < 10; ++i) {
            int halfIcon = i * 2 + 1;
            int x = left - i * 8;
            if (halfIcon < thirstSaturationInt) {
               RenderUtil.drawTexturedModalRect((float)x, (float)top, 126, 0, 9, 9);
            } else if (halfIcon == thirstSaturationInt) {
               RenderUtil.drawTexturedModalRect((float)x, (float)top, 135, 0, 9, 9);
            }
         }
      }

      GlStateManager.disableBlend();
   }

   private void bind(ResourceLocation resource) {
      this.minecraftInstance.getTextureManager().bindTexture(resource);
   }
}
