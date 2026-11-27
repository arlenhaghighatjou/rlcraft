package ichttt.mods.firstaid.client.util;

import net.lax1dude.eaglercraft.v1_8.HString;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerModelRenderer {
   private static final ResourceLocation HEALTH_RENDER_LOCATION = new ResourceLocation("firstaid", "textures/gui/simple_health.png");
   private static final ResourceLocation HEALTH_RENDER_LOCATION_OLD = new ResourceLocation("firstaid", "textures/gui/simple_health_old.png");
   private static final int SIZE = 32;
   private static int angle = 0;
   private static boolean otherWay = false;
   private static int cooldown = 0;

   public PlayerModelRenderer() {
   }

   public static void renderPlayerHealth(AbstractPlayerDamageModel damageModel, boolean fourColors, boolean oldModel, Gui gui, boolean flashState, float alpha, float partialTicks) {
      int yOffset = flashState ? 64 : 0;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F - alpha / 255.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(oldModel ? HEALTH_RENDER_LOCATION_OLD : HEALTH_RENDER_LOCATION);
      if (FirstAidConfig.overlay.enableEasterEggs && (EventCalendar.isAFDay() || EventCalendar.isHalloween())) {
         float angle = (float)PlayerModelRenderer.angle;
         if (cooldown == 0) {
            angle += (otherWay ? -partialTicks : partialTicks) * 2.0F;
         }

         if (FirstAidConfig.overlay.pos != FirstAidConfig.Overlay.Position.BOTTOM_LEFT && FirstAidConfig.overlay.pos != FirstAidConfig.Overlay.Position.TOP_LEFT) {
            GlStateManager.translate(angle * 0.5F, 0.0F, 0.0F);
         } else {
            GlStateManager.translate(angle * 1.5F, 0.0F, 0.0F);
         }

         GlStateManager.rotate(angle, 0.0F, 0.0F, 1.0F);
      }

      if (yOffset != 0) {
         GlStateManager.translate(0.0F, (float)(-yOffset), 0.0F);
      }

      drawPart(gui, fourColors, damageModel.HEAD, 8, yOffset + 0, 16, 16);
      drawPart(gui, fourColors, damageModel.BODY, 8, yOffset + 16, 16, 24);
      drawPart(gui, fourColors, damageModel.LEFT_ARM, 0, yOffset + 16, 8, 24);
      drawPart(gui, fourColors, damageModel.RIGHT_ARM, 24, yOffset + 16, 8, 24);
      drawPart(gui, fourColors, damageModel.LEFT_LEG, 8, yOffset + 40, 8, 16);
      drawPart(gui, fourColors, damageModel.RIGHT_LEG, 16, yOffset + 40, 8, 16);
      drawPart(gui, fourColors, damageModel.LEFT_FOOT, 8, yOffset + 56, 8, 8);
      drawPart(gui, fourColors, damageModel.RIGHT_FOOT, 16, yOffset + 56, 8, 8);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   private static void drawPart(Gui gui, boolean fourColors, AbstractDamageablePart part, int texX, int texY, int sizeX, int sizeY) {
      int rawTexX = texX;
      texX = texX + 32 * getState(part, fourColors);
      gui.drawTexturedModalRect(rawTexX, texY, texX, texY, sizeX, sizeY);
   }

   private static int getState(AbstractDamageablePart part, boolean fourColors) {
      if (part.currentHealth <= 0.001F) {
         return 5;
      } else {
         int maxHealth = part.getMaxHealth();
         if (Math.abs(part.currentHealth - (float)maxHealth) < 0.001F) {
            return 0;
         } else {
            float healthPercentage = part.currentHealth / (float)maxHealth;
            if (healthPercentage < 1.0F && healthPercentage > 0.0F) {
               if (!fourColors && healthPercentage > 0.75F) {
                  return 1;
               } else if (healthPercentage > 0.5F) {
                  return 2;
               } else {
                  return !fourColors && healthPercentage > 0.25F ? 3 : 4;
               }
            } else {
               throw new RuntimeException(HString.format("Calculated invalid health for part %s with current health %s and max health %d. Got value %s", part.part, part.currentHealth, maxHealth, healthPercentage));
            }
         }
      }
   }

   public static void tickFun() {
      if (cooldown > 0) {
         --cooldown;
      } else {
         angle += otherWay ? -2 : 2;
         if (angle >= 90 || angle <= 0) {
            otherWay = !otherWay;
            if (!otherWay) {
               cooldown = 200;
            } else {
               cooldown = 30;
            }
         }

      }
   }
}
