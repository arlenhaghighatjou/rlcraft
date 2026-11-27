package dev.satyrn.wolfarmor.client.renderer;

import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.api.entity.IFoodStatsCreature;
import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.api.util.CreatureFoodStats;
import dev.satyrn.wolfarmor.util.WolfFoodStatsLevel;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.arlenh7.eaglercraft.lwjgl.Point;

@SideOnly(Side.CLIENT)
public class WolfRenderHelper {
   private static final int ICON_SIZE = 9;
   private static final Point ARMOR_FULL = new Point(34, 9);
   private static final Point ARMOR_HALF = new Point(25, 9);
   private static final Point ARMOR_EMPTY = new Point(16, 9);
   private static final int HEART_POISON_OFFSET_X = 36;
   private static final int HEART_WITHER_OFFSET_X = 72;
   private static final int HEART_ABSORPTION_FULL_OFFSET_X = 144;
   private static final int HEART_ABSORPTION_HALF_OFFSET_X = 153;
   private static final int HEART_FULL_OFFSET = 36;
   private static final int HEART_HALF_OFFSET = 45;
   private static final Point HEART_EMPTY = new Point(16, 0);
   private static final int FOOD_HUNGER_OFFSET_X = 36;
   private static final int FOOD_HUNGER_BG_MULT_X = 13;
   private static final int FOOD_FULL_OFFSET_X = 36;
   private static final int FOOD_HALF_OFFSET_X = 45;
   private static final Point FOOD_EMPTY = new Point(16, 27);

   public WolfRenderHelper() {
   }

   public static void drawStats(@Nonnull EntityWolf wolf, boolean renderNameplate, double x, double y, double z, float playerViewX, float playerViewY, boolean isThirdPerson, boolean isSneaking) {
      if (wolf.isTamed()) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(x, y, z);
         EaglercraftGPU.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate((isThirdPerson ? -1.0F : 1.0F) * playerViewX, 1.0F, 0.0F, 0.0F);
         GlStateManager.scale(-0.01F, -0.01F, 0.01F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.depthFunc(519);
         renderStats(wolf, 5, renderNameplate ? 22 : 0);
         GlStateManager.depthFunc(515);
         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
      }
   }

   private static void drawIcon(int x, int y, int texX, int texY) {
      float pixelScale = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer builder = tessellator.getBuffer();
      builder.begin(7, DefaultVertexFormats.POSITION_TEX);
      builder.pos((double)x, (double)(y + 9), 0.0D).tex((double)((float)texX * 0.00390625F), (double)((float)(texY + 9) * 0.00390625F)).endVertex();
      builder.pos((double)(x + 9), (double)(y + 9), 0.0D).tex((double)((float)(texX + 9) * 0.00390625F), (double)((float)(texY + 9) * 0.00390625F)).endVertex();
      builder.pos((double)(x + 9), (double)y, 0.0D).tex((double)((float)(texX + 9) * 0.00390625F), (double)((float)texY * 0.00390625F)).endVertex();
      builder.pos((double)x, (double)y, 0.0D).tex((double)((float)texX * 0.00390625F), (double)((float)texY * 0.00390625F)).endVertex();
      tessellator.draw();
   }

   private static void drawReversedIcon(int x, int y, int texX, int texY) {
      float pixelScale = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer builder = tessellator.getBuffer();
      builder.begin(7, DefaultVertexFormats.POSITION_TEX);
      builder.pos((double)x, (double)(y + 9), 0.0D).tex((double)((float)(texX + 9) * pixelScale), (double)((float)(texY + 9) * pixelScale)).endVertex();
      builder.pos((double)(x + 9), (double)(y + 9), 0.0D).tex((double)((float)texX * pixelScale), (double)((float)(texY + 9) * pixelScale)).endVertex();
      builder.pos((double)(x + 9), (double)y, 0.0D).tex((double)((float)texX * pixelScale), (double)((float)texY * pixelScale)).endVertex();
      builder.pos((double)x, (double)y, 0.0D).tex((double)((float)(texX + 9) * pixelScale), (double)((float)texY * pixelScale)).endVertex();
      tessellator.draw();
   }

   public static void drawIcon(int x, int y, int texX, int texY, boolean reverse) {
      if (reverse) {
         drawReversedIcon(x, y, texX, texY);
      } else {
         drawIcon(x, y, texX, texY);
      }

   }

   private static void renderStats(EntityWolf wolf, int x, int y) {
      int armorValue = wolf.getTotalArmorValue();
      int iconWidth = 9;
      int iconCount = 10;
      int xOffset = x + -iconWidth * iconCount / 2;
      int yOffset = y;
      if (((IArmoredWolf)wolf).getHasArmor() && armorValue > 0) {
         Point renderPos = renderArmor(wolf, new Point(xOffset, y), false);
         yOffset = renderPos.getY();
      }

      Point renderPos = renderHealth(wolf, new Point(xOffset, yOffset), false);
      yOffset = renderPos.getY();
      if (WolfArmorMod.getConfig().getFoodStatsLevel() != WolfFoodStatsLevel.DISABLED) {
         renderHunger(wolf, new Point(xOffset, yOffset), false);
      }

   }

   public static Point renderArmor(EntityWolf wolf, Point coord, boolean reverse) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
      int armorValue = wolf.getTotalArmorValue();
      int xInitial = reverse ? coord.getX() + 81 : coord.getX();

      for(int index = 0; index < 10; ++index) {
         int xOffset = reverse ? xInitial - index * 8 : xInitial + index * 8;
         if (index * 2 + 1 < armorValue) {
            drawIcon(xOffset, coord.getY(), ARMOR_FULL.getX(), ARMOR_FULL.getY(), reverse);
         } else if (index * 2 + 1 == armorValue) {
            drawIcon(xOffset, coord.getY(), ARMOR_HALF.getX(), ARMOR_HALF.getY(), reverse);
         } else if (index * 2 + 1 > armorValue) {
            drawIcon(xOffset, coord.getY(), ARMOR_EMPTY.getX(), ARMOR_EMPTY.getY(), reverse);
         }
      }

      return new Point(81 + coord.getX(), 10 + coord.getY());
   }

   public static Point renderHealth(EntityWolf wolf, Point coord, boolean reverse) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
      int health = MathHelper.ceil(wolf.getHealth());
      double maxHealth = wolf.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
      int absorption = MathHelper.ceil(wolf.getAbsorptionAmount());
      int heartRows = MathHelper.ceil((maxHealth + (double)absorption) / 2.0D / 10.0D);
      int heartHeight = Math.max(10 - (heartRows - 2), 3);
      int xInitial = reverse ? coord.getX() + 81 : coord.getX();
      int currentAbsorption = absorption;
      int regenHeartIndex = -1;
      if (wolf.isPotionActive(MobEffects.REGENERATION)) {
         regenHeartIndex = wolf.ticksExisted % MathHelper.ceil(maxHealth + 5.0D);
      }

      for(int index = MathHelper.ceil((maxHealth + (double)absorption) / 2.0D) - 1; index >= 0; --index) {
         int texX = HEART_EMPTY.getX();
         if (wolf.isPotionActive(MobEffects.POISON)) {
            texX += 36;
         } else if (wolf.isPotionActive(MobEffects.WITHER)) {
            texX += 72;
         }

         int heartRow = MathHelper.ceil(((float)index + 1.0F) / 10.0F) - 1;
         int xOffset = reverse ? xInitial - index % 10 * 8 : xInitial + index % 10 * 8;
         int yOffset = coord.getY() + heartRow * heartHeight;
         if (health <= 2) {
            yOffset += wolf.getRNG().nextInt(2);
         }

         if (currentAbsorption <= 0 && index == regenHeartIndex) {
            yOffset -= 2;
         }

         drawIcon(xOffset, yOffset, HEART_EMPTY.getX(), HEART_EMPTY.getY(), reverse);
         if (currentAbsorption > 0) {
            if (currentAbsorption == absorption && absorption % 2 == 1) {
               drawIcon(xOffset, yOffset, texX + 153, HEART_EMPTY.getY(), reverse);
               --currentAbsorption;
            } else {
               drawIcon(xOffset, yOffset, texX + 144, HEART_EMPTY.getY(), reverse);
               currentAbsorption -= 2;
            }
         } else if (index * 2 + 1 < health) {
            drawIcon(xOffset, yOffset, texX + 36, HEART_EMPTY.getY(), reverse);
         } else if (index * 2 + 1 == health) {
            drawIcon(xOffset, yOffset, texX + 45, HEART_EMPTY.getY(), reverse);
         }
      }

      return new Point(81 + coord.getX(), heartHeight - 1 + coord.getY());
   }

   public static Point renderHunger(EntityWolf wolf, Point coord, boolean reverse) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
      CreatureFoodStats foodStats = ((IFoodStatsCreature)wolf).getFoodStats();
      int foodLevel = foodStats.getFoodLevel();
      int xInitial = reverse ? coord.getX() + 81 : coord.getX();

      for(int index = 0; index < 10; ++index) {
         int xOffset = reverse ? xInitial - index * 8 : xInitial + index * 8;
         int yOffset = coord.getY();
         int texX = FOOD_EMPTY.getX();
         int hungerOffset = 0;
         if (wolf.isPotionActive(MobEffects.HUNGER)) {
            texX += 36;
            hungerOffset = 13;
         }

         if (foodStats.getSaturationLevel() <= 0.0F && wolf.ticksExisted % (foodLevel * 3 + 1) == 0) {
            yOffset += wolf.getRNG().nextInt(3) - 1;
         }

         drawIcon(xOffset, yOffset, FOOD_EMPTY.getX() + hungerOffset * 9, FOOD_EMPTY.getY(), !reverse);
         if (index * 2 + 1 < foodLevel) {
            drawIcon(xOffset, yOffset, texX + 36, FOOD_EMPTY.getY(), !reverse);
         } else if (index * 2 + 1 == foodLevel) {
            drawIcon(xOffset, yOffset, texX + 45, FOOD_EMPTY.getY(), !reverse);
         }
      }

      return new Point(81 + coord.getX(), 10 + coord.getY());
   }
}
