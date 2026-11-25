package net.theawesomegem.fishingmadebetter.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.FishingCapabilityProvider;
import net.theawesomegem.fishingmadebetter.common.capability.fishing.IFishingData;
import net.theawesomegem.fishingmadebetter.common.configuration.ConfigurationManager;

public class GuiReelingOverlay extends Gui {
   private static final ResourceLocation textureOutline = new ResourceLocation("fishingmadebetter", "textures/gui/reeling_hud_outline.png");
   private static final ResourceLocation textureTarget = new ResourceLocation("fishingmadebetter", "textures/gui/reeling_hud_target.png");
   private static final ResourceLocation texturePosition = new ResourceLocation("fishingmadebetter", "textures/gui/reeling_hud_position.png");
   private static final ResourceLocation textureUnderOverlay = new ResourceLocation("fishingmadebetter", "textures/gui/reeling_hud_underoverlay.png");
   private static final ResourceLocation textureFullsize = new ResourceLocation("fishingmadebetter", "textures/gui/reeling_hud_fullsize.png");
   private static final ResourceLocation textureBiome = new ResourceLocation("fishingmadebetter", "textures/gui/reeling_hud_biome.png");
   private Minecraft mc;
   private final int backgroundBarWidth = 128;
   private final int backgroundBarHeight = 24;
   private final int outlineBarWidth = 134;
   private final int outlineBarHeight = 30;
   private int fontColor = 16777215;
   private FontRenderer fontRenderer;

   public GuiReelingOverlay(Minecraft minecraft) {
      this.mc = minecraft;
      this.fontRenderer = minecraft.fontRenderer;
   }

   @SubscribeEvent
   public void onRenderGameOverlay(Post e) {
      if (!e.isCanceled() && e.getType() == ElementType.ALL && ConfigurationManager.client.reelingHud) {
         EntityPlayer player = this.mc.player;
         if (player != null && player.isEntityAlive()) {
            IFishingData fishingData = (IFishingData)player.getCapability(FishingCapabilityProvider.FISHING_DATA_CAP, (EnumFacing)null);
            if (fishingData != null && fishingData.isFishing()) {
               int scaledWidth = e.getResolution().getScaledWidth();
               int scaledHeight = e.getResolution().getScaledHeight();
               double distance = (double)(fishingData.getFishDeepLevel() - fishingData.getFishDistance()) / 10.0D;
               this.fontColor = this.getIntFromColor(fishingData.getLineBreak());
               this.fontRenderer.drawStringWithShadow(I18n.format("fishingmadebetter.reelingoverlay.distance", new Object[0]) + String.format(": %sm", distance), (float)this.getBarPosX(scaledWidth) + 67.0F - 30.0F, (float)(this.getBarPosY(scaledHeight) + 30 + 2), this.fontColor);
               int posX = this.getBarPosX(scaledWidth);
               int posY = this.getBarPosY(scaledHeight);
               posX = posX + ConfigurationManager.client.reelingMeterXOffset;
               posY = posY + ConfigurationManager.client.reelingMeterYOffset;
               Integer[] minigameBackground = fishingData.getMinigameBackground();
               this.beginRenderingTransparency();
               this.mc.renderEngine.bindTexture(textureOutline);
               this.renderOutlineBar(posX, posY);
               if (minigameBackground[0].intValue() == 1) {
                  this.mc.renderEngine.bindTexture(textureUnderOverlay);
                  this.renderBackgroundBar(posX, posY, 0, minigameBackground[3].intValue() == 0 ? 7 : minigameBackground[3].intValue());
                  this.mc.renderEngine.bindTexture(textureFullsize);
                  this.renderBackgroundBar(posX, posY, 0, 0);
               } else if (minigameBackground[0].intValue() == -1) {
                  this.mc.renderEngine.bindTexture(textureUnderOverlay);
                  this.renderBackgroundBar(posX, posY, 0, minigameBackground[3].intValue());
                  this.mc.renderEngine.bindTexture(textureFullsize);
                  this.renderBackgroundBar(posX, posY, 0, 1);
               } else if (minigameBackground[1].intValue() == 1) {
                  this.mc.renderEngine.bindTexture(textureUnderOverlay);
                  this.renderBackgroundBar(posX, posY, 0, minigameBackground[3].intValue() == 0 ? 9 : minigameBackground[3].intValue());
                  this.mc.renderEngine.bindTexture(textureFullsize);
                  this.renderBackgroundBar(posX, posY, 0, 2);
               } else {
                  if (minigameBackground[3].intValue() == 0) {
                     this.mc.renderEngine.bindTexture(textureUnderOverlay);
                     this.renderBackgroundBar(posX, posY, 1, minigameBackground[4].intValue());
                  } else {
                     this.mc.renderEngine.bindTexture(textureUnderOverlay);
                     this.renderBackgroundBar(posX, posY, 0, minigameBackground[3].intValue());
                  }

                  this.mc.renderEngine.bindTexture(textureUnderOverlay);
                  this.renderBackgroundBar(posX, posY, 0, minigameBackground[2].intValue() + 3);
                  this.mc.renderEngine.bindTexture(textureBiome);
                  this.renderBackgroundBar(posX, posY, 0, minigameBackground[4].intValue());
                  this.mc.renderEngine.bindTexture(textureUnderOverlay);
                  this.renderBackgroundBar(posX, posY, 0, minigameBackground[2].intValue() + 5);
               }

               this.mc.renderEngine.bindTexture(textureTarget);
               this.renderReelingTarget(fishingData, posX, posY);
               this.mc.renderEngine.bindTexture(texturePosition);
               this.renderReelingPosition(fishingData, posX, posY);
               this.endRenderingTransparency();
            }
         }
      }
   }

   public int getIntFromColor(int input) {
      int R = (int)Math.min(255.0D, (double)input * 8.5D);
      int G = (int)Math.min(255.0D, 510.0D - (double)input * 8.5D);
      int B = 0;
      R = R << 16 & 16711680;
      G = G << 8 & '\uff00';
      B = B & 255;
      return -16777216 | R | G | B;
   }

   private void renderOutlineBar(int posX, int posY) {
      this.drawTexturedModalRect(posX - 3, posY - 3, 0, 0, 134, 30);
   }

   private void renderBackgroundBar(int posX, int posY, int indexX, int indexY) {
      this.drawTexturedModalRect(posX, posY, 128 * indexX, 24 * indexY, 128, 24);
   }

   private void renderReelingPosition(IFishingData fishingData, int posX, int posY) {
      int reelAmount = fishingData.getReelAmount() / 10;
      int variance = fishingData.getErrorVariance() / 10;
      int width = variance * 2;
      drawModalRectWithCustomSizedTexture(posX + 16 + reelAmount - variance, posY + 13, 0.0F, 0.0F, 2, 8, 8.0F, 8.0F);
      if (width - 4 > 0) {
         drawScaledCustomSizeModalRect(posX + 16 + reelAmount - variance + 2, posY + 13, 2.0F, 0.0F, 4, 8, width - 4, 8, 8.0F, 8.0F);
      }

      drawModalRectWithCustomSizedTexture(posX + 16 + reelAmount + variance - 2, posY + 13, 6.0F, 0.0F, 2, 8, 8.0F, 8.0F);
   }

   private void renderReelingTarget(IFishingData fishingData, int posX, int posY) {
      int reelTarget = fishingData.getReelTarget() / 10;
      drawModalRectWithCustomSizedTexture(posX + 16 + reelTarget - 3, posY + 14, 0.0F, 0.0F, 6, 6, 6.0F, 6.0F);
   }

   private void beginRenderingTransparency() {
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      GlStateManager.enableAlpha();
      GlStateManager.enableBlend();
   }

   private void endRenderingTransparency() {
      GlStateManager.popMatrix();
   }

   private int getBarPosX(int scaledWidth) {
      switch(ConfigurationManager.client.reelingHubPos) {
      case TOP_LEFT:
      case BOTTOM_LEFT:
         return 0;
      case TOP_RIGHT:
      case BOTTOM_RIGHT:
         return scaledWidth - 128;
      case TOP_CENTER:
      case BOTTOM_CENTER:
         return scaledWidth / 2 - 64;
      default:
         return 0;
      }
   }

   private int getBarPosY(int scaledHeight) {
      switch(ConfigurationManager.client.reelingHubPos) {
      case TOP_LEFT:
      case TOP_RIGHT:
      case TOP_CENTER:
         return 0;
      case BOTTOM_LEFT:
      case BOTTOM_RIGHT:
      case BOTTOM_CENTER:
         return scaledHeight - 24;
      default:
         return 0;
      }
   }
}
