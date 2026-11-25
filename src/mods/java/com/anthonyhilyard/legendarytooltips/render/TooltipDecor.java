package com.anthonyhilyard.legendarytooltips.render;

import com.anthonyhilyard.legendarytooltips.LegendaryTooltipsConfig;
import com.anthonyhilyard.legendarytooltips.util.GuiHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;

public class TooltipDecor {
   private static final ResourceLocation TEXTURE_TOOLTIP_BORDERS = new ResourceLocation("legendarytooltips", "textures/gui/tooltip_borders.png");
   private static int currentTooltipBorderStart = 0;
   private static int currentTooltipBorderEnd = 0;
   private static Map<Integer, Integer> shineTimers = new HashMap<Integer, Integer>() {
      {
         this.put(Integer.valueOf(0), Integer.valueOf(0));
      }
   };
   private static Map<Integer, List<String>> cachedPreWrapLines = new HashMap();

   public TooltipDecor() {
   }

   public static void setCurrentTooltipBorderStart(int color) {
      currentTooltipBorderStart = color;
   }

   public static void setCurrentTooltipBorderEnd(int color) {
      currentTooltipBorderEnd = color;
   }

   public static void setCachedLines(List<String> lines, int index) {
      cachedPreWrapLines.put(Integer.valueOf(index), lines);
   }

   public static void updateTimers() {
      for(Integer index : shineTimers.keySet()) {
         shineTimers.put(index, Integer.valueOf(((Integer)shineTimers.get(index)).intValue() - 1));
      }

   }

   public static void resetTimer(int index) {
      shineTimers.put(Integer.valueOf(index), Integer.valueOf(LegendaryTooltipsConfig.INSTANCE.shineTicks));
   }

   public static void resetTimers() {
      for(Integer index : shineTimers.keySet()) {
         resetTimer(index.intValue());
      }

   }

   public static void drawShadow(int x, int y, int width, int height) {
      int shadowColor = 1140850688;
      GuiUtils.drawGradientRect(390, x - 1, y + height + 4, x + width + 4, y + height + 5, shadowColor, shadowColor);
      GuiUtils.drawGradientRect(390, x + width + 4, y - 1, x + width + 5, y + height + 5, shadowColor, shadowColor);
      GuiUtils.drawGradientRect(390, x + width + 3, y + height + 3, x + width + 4, y + height + 4, shadowColor, shadowColor);
      GuiUtils.drawGradientRect(390, x, y + height + 5, x + width + 5, y + height + 6, shadowColor, shadowColor);
      GuiUtils.drawGradientRect(390, x + width + 5, y, x + width + 6, y + height + 5, shadowColor, shadowColor);
   }

   public static void drawSeparator(int x, int y, int width, int color) {
      GuiHelper.drawGradientRectHorizontal(402, x, y, x + width / 2, y + 1, color & 16777215, color);
      GuiHelper.drawGradientRectHorizontal(402, x + width / 2, y, x + width, y + 1, color, color & 16777215);
   }

   private static double lerp(double t, double a, double b) {
      return a + t * (b - a);
   }

   public static void drawBorder(int x, int y, int width, int height, ItemStack item, List<String> lines, FontRenderer font, int frameLevel, boolean comparison, int index) {
      if (comparison) {
         GuiUtils.drawGradientRect(400, x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, currentTooltipBorderStart, currentTooltipBorderEnd);
         GuiUtils.drawGradientRect(400, x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, currentTooltipBorderStart, currentTooltipBorderEnd);
         GuiUtils.drawGradientRect(400, x - 3, y - 3, x + width + 3, y - 3 + 1, currentTooltipBorderStart, currentTooltipBorderStart);
         GuiUtils.drawGradientRect(400, x - 3, y + height + 2, x + width + 3, y + height + 3, currentTooltipBorderEnd, currentTooltipBorderEnd);
         drawSeparator(x - 3 + 1, y - 3 + 1 + 10, width, currentTooltipBorderStart);
      }

      if (LegendaryTooltipsConfig.INSTANCE.nameSeparator && item != null && !item.isEmpty()) {
         String textLine = null;
         if (cachedPreWrapLines.containsKey(Integer.valueOf(index))) {
            textLine = (String)((List)cachedPreWrapLines.get(Integer.valueOf(index))).get(0);
         } else if (cachedPreWrapLines.containsKey(Integer.valueOf(0))) {
            index = 0;
            textLine = (String)((List)cachedPreWrapLines.get(Integer.valueOf(0))).get(0);
         }

         if (textLine != null) {
            List<String> wrappedLine = font.listFormattedStringToWidth(textLine, width);
            int titleLineCount = wrappedLine.size();
            if (((List)cachedPreWrapLines.get(Integer.valueOf(index))).size() > titleLineCount) {
               int offset = 0;
               if (comparison) {
                  offset = 11;
               }

               drawSeparator(x - 3 + 1, y - 3 + 1 + titleLineCount * 10 + 1 + offset, width, currentTooltipBorderStart);
            }
         }
      }

      if (frameLevel < 16 && frameLevel >= 0) {
         Minecraft mc = Minecraft.getMinecraft();
         mc.getTextureManager().bindTexture(TEXTURE_TOOLTIP_BORDERS);
         int textureWidth = GlStateManager.glGetTexLevelParameteri(3553, 0, 4096);
         int textureHeight = GlStateManager.glGetTexLevelParameteri(3553, 0, 4097);
         if (LegendaryTooltipsConfig.INSTANCE.shineEffect) {
            if (!shineTimers.containsKey(Integer.valueOf(index))) {
               shineTimers.put(Integer.valueOf(index), Integer.valueOf(LegendaryTooltipsConfig.INSTANCE.shineTicks));
            }

            int maxTick = LegendaryTooltipsConfig.INSTANCE.shineTicks;
            if ((float)((Integer)shineTimers.get(Integer.valueOf(index))).intValue() >= (float)maxTick * 0.2F && (float)((Integer)shineTimers.get(Integer.valueOf(index))).intValue() <= (float)maxTick * 0.8F) {
               float interval = MathHelper.clamp(((float)((Integer)shineTimers.get(Integer.valueOf(index))).intValue() - (float)maxTick * 0.2F) / ((float)maxTick * 0.6F), 0.0F, 1.0F);
               int alpha = (int)(153.0F * interval) << 24;
               int horizontalMin = x - 3;
               int horizontalMax = x + width + 3;
               int horizontalInterval = (int)lerp((double)(interval * interval), (double)horizontalMax, (double)horizontalMin);
               GuiHelper.drawGradientRectHorizontal(402, Math.max(horizontalInterval - 36, horizontalMin), y - 3, Math.min(horizontalInterval, horizontalMax), y - 3 + 1, 16777215, 16777215 | alpha);
               GuiHelper.drawGradientRectHorizontal(402, Math.max(horizontalInterval, horizontalMin), y - 3, Math.min(horizontalInterval + 36, horizontalMax), y - 3 + 1, 16777215 | alpha, 16777215);
            }

            if (LegendaryTooltipsConfig.INSTANCE.shineSync) {
               if ((float)((Integer)shineTimers.get(Integer.valueOf(index))).intValue() >= (float)maxTick * 0.2F && (float)((Integer)shineTimers.get(Integer.valueOf(index))).intValue() <= (float)maxTick * 0.8F) {
                  float interval = MathHelper.clamp(((float)((Integer)shineTimers.get(Integer.valueOf(index))).intValue() - (float)maxTick * 0.2F) / ((float)maxTick * 0.6F), 0.0F, 1.0F);
                  int alpha = (int)(85.0F * interval) << 24;
                  int verticalMin = y - 3 + 1;
                  int verticalMax = y + height + 3 - 1;
                  int verticalInterval = (int)lerp((double)(interval * interval), (double)verticalMax, (double)verticalMin);
                  GuiUtils.drawGradientRect(402, x - 3, Math.max(verticalInterval - 12, verticalMin), x - 3 + 1, Math.min(verticalInterval, verticalMax), 16777215, 16777215 | alpha);
                  GuiUtils.drawGradientRect(402, x - 3, Math.max(verticalInterval, verticalMin), x - 3 + 1, Math.min(verticalInterval + 12, verticalMax), 16777215 | alpha, 16777215);
               }
            } else if ((float)((Integer)shineTimers.get(Integer.valueOf(index))).intValue() <= (float)maxTick * 0.4F) {
               float interval = MathHelper.clamp((float)((Integer)shineTimers.get(Integer.valueOf(index))).intValue() / ((float)maxTick * 0.4F), 0.0F, 1.0F);
               int alpha = (int)(85.0F * interval) << 24;
               int verticalMin = y - 3 + 1;
               int verticalMax = y + height + 3 - 1;
               int verticalInterval = (int)lerp((double)(interval * interval), (double)verticalMax, (double)verticalMin);
               GuiUtils.drawGradientRect(402, x - 3, Math.max(verticalInterval - 12, verticalMin), x - 3 + 1, Math.min(verticalInterval, verticalMax), 16777215, 16777215 | alpha);
               GuiUtils.drawGradientRect(402, x - 3, Math.max(verticalInterval, verticalMin), x - 3 + 1, Math.min(verticalInterval + 12, verticalMax), 16777215 | alpha, 16777215);
            }

            if (((Integer)shineTimers.get(Integer.valueOf(index))).intValue() <= 0 && LegendaryTooltipsConfig.INSTANCE.shineRepeat) {
               resetTimers();
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0D, 0.0D, 410.0D);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         Gui.drawModalRectWithCustomSizedTexture(x - 6, y - 6, (float)(frameLevel / 8 * 64), (float)(frameLevel * 16 % textureHeight), 8, 8, (float)textureWidth, (float)textureHeight);
         Gui.drawModalRectWithCustomSizedTexture(x + width - 8 + 6, y - 6, (float)(56 + frameLevel / 8 * 64), (float)(frameLevel * 16 % textureHeight), 8, 8, (float)textureWidth, (float)textureHeight);
         Gui.drawModalRectWithCustomSizedTexture(x - 6, y + height - 8 + 6, (float)(frameLevel / 8 * 64), (float)(frameLevel * 16 % textureHeight + 8), 8, 8, (float)textureWidth, (float)textureHeight);
         Gui.drawModalRectWithCustomSizedTexture(x + width - 8 + 6, y + height - 8 + 6, (float)(56 + frameLevel / 8 * 64), (float)(frameLevel * 16 % textureHeight + 8), 8, 8, (float)textureWidth, (float)textureHeight);
         if (width >= 48) {
            Gui.drawModalRectWithCustomSizedTexture(x + width / 2 - 24, y - 9, (float)(8 + frameLevel / 8 * 64), (float)(frameLevel * 16 % textureHeight), 48, 8, (float)textureWidth, (float)textureHeight);
            Gui.drawModalRectWithCustomSizedTexture(x + width / 2 - 24, y + height - 8 + 9, (float)(8 + frameLevel / 8 * 64), (float)(frameLevel * 16 % textureHeight + 8), 48, 8, (float)textureWidth, (float)textureHeight);
         }

         GlStateManager.popMatrix();
      }
   }
}
