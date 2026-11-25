package net.silentchaos512.scalinghealth.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.lib.event.ClientTicks;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.lib.EnumAreaDifficultyMode;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;

public class DifficultyDisplayHandler extends Gui {
   public static final DifficultyDisplayHandler INSTANCE = new DifficultyDisplayHandler();
   private static final ResourceLocation TEXTURE = new ResourceLocation("scalinghealth", "textures/gui/hud.png");
   private int lastDifficultyDisplayed = -100;
   private int lastAreaDifficultyDisplayed = -100;
   private int lastUpdateTime = Integer.MIN_VALUE;

   public DifficultyDisplayHandler() {
   }

   public void showBar() {
      this.lastUpdateTime = Integer.MIN_VALUE;
      this.lastDifficultyDisplayed = -100;
   }

   @SubscribeEvent
   public void onRenderOverlay(Post event) {
      if (event.getType() == ElementType.TEXT && Config.Difficulty.maxValue > 0.0F && Config.Client.Difficulty.renderMeter) {
         Minecraft mc = Minecraft.getMinecraft();
         EntityPlayer player = mc.player;
         int width = event.getResolution().getScaledWidth();
         int height = event.getResolution().getScaledHeight();
         SHPlayerDataHandler.PlayerData data = player != null ? SHPlayerDataHandler.get(player) : null;
         if (data != null) {
            int difficulty = (int)data.getDifficulty();
            int areaDifficultyUnclamped = (int)Config.Difficulty.AREA_DIFFICULTY_MODE.getAreaDifficulty(player.world, player.getPosition());
            int areaDifficulty = MathHelper.clamp(areaDifficultyUnclamped, 0, (int)Config.Difficulty.maxValue);
            if (Config.Difficulty.AREA_DIFFICULTY_MODE == EnumAreaDifficultyMode.SERVER_WIDE) {
               difficulty = areaDifficulty;
            }

            int timeSinceLastUpdate = ClientTicks.ticksInGame - this.lastUpdateTime;
            if (difficulty != this.lastDifficultyDisplayed) {
               this.lastDifficultyDisplayed = difficulty;
               this.lastUpdateTime = ClientTicks.ticksInGame;
            }

            if (areaDifficulty < this.lastAreaDifficultyDisplayed - 10 || areaDifficulty > this.lastAreaDifficultyDisplayed + 10 && timeSinceLastUpdate > 1200) {
               this.lastAreaDifficultyDisplayed = areaDifficulty;
               this.lastUpdateTime = ClientTicks.ticksInGame;
            }

            int currentTime = ClientTicks.ticksInGame;
            if (Config.Client.Difficulty.renderMeterAlways || currentTime - this.lastUpdateTime < Config.Client.Difficulty.meterDisplayTime) {
               GlStateManager.enableBlend();
               mc.renderEngine.bindTexture(TEXTURE);
               GlStateManager.pushMatrix();
               int posX = Config.Client.Difficulty.meterPosX;
               if (posX < 0) {
                  posX = posX + width - 64;
               }

               int posY = Config.Client.Difficulty.meterPosY;
               if (posY < 0) {
                  posY = posY + height - 12;
               }

               this.drawTexturedModalRect(posX, posY, 190, 0, 66, 14, 16777215);
               int barLength = (int)((float)(60 * areaDifficulty) / Config.Difficulty.maxValue);
               this.drawTexturedModalRect(posX + 3, posY + 5, 193, 19, barLength, 6, 16777215);
               barLength = (int)((float)(60 * difficulty) / Config.Difficulty.maxValue);
               this.drawTexturedModalRect(posX + 3, posY + 3, 193, 17, barLength, 2, 16777215);
               GlStateManager.pushMatrix();
               float textScale = 0.6F;
               GlStateManager.scale(textScale, textScale, 1.0F);
               String localizedString = ScalingHealth.i18n.miscText("difficultyMeterText", new Object[0]);
               mc.fontRenderer.drawStringWithShadow(localizedString, (float)posX / textScale + 4.0F, (float)posY / textScale - 9.0F, 16777215);
               String str = String.format("%d", areaDifficultyUnclamped);
               int strWidth = mc.fontRenderer.getStringWidth(str);
               mc.fontRenderer.drawStringWithShadow(str, (float)posX / textScale + 104.0F - (float)strWidth, (float)posY / textScale - 9.0F, 11184810);
               GlStateManager.popMatrix();
               GlStateManager.popMatrix();
               GlStateManager.disableBlend();
            }

         }
      }
   }

   private void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int color) {
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      GlStateManager.color(r, g, b);
      this.drawTexturedModalRect(x, y, textureX, textureY, width, height);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
   }
}
