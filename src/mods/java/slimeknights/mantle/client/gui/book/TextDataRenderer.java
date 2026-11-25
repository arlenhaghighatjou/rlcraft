package slimeknights.mantle.client.gui.book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.data.element.TextData;

@SideOnly(Side.CLIENT)
public class TextDataRenderer {
   public TextDataRenderer() {
   }

   /** @deprecated */
   public static String drawText(int x, int y, int boxWidth, int boxHeight, TextData[] data, int mouseX, int mouseY, FontRenderer fr) {
      List<String> tooltip = new ArrayList();
      String action = drawText(x, y, boxWidth, boxHeight, data, mouseX, mouseY, fr, tooltip);
      if (tooltip.size() > 0) {
         drawTooltip(tooltip, mouseX, mouseY, fr);
      }

      return action;
   }

   public static String drawText(int x, int y, int boxWidth, int boxHeight, TextData[] data, int mouseX, int mouseY, FontRenderer fr, @Nonnull List<String> tooltip) {
      String action = "";
      int atX = x;
      int atY = y;
      float prevScale = 1.0F;

      for(TextData item : data) {
         int box1W = 9999;
         int box1H = y + fr.FONT_HEIGHT;
         int box2Y = 9999;
         int box3X = 9999;
         int box3Y = 9999;
         if (item != null && item.text != null && !item.text.isEmpty()) {
            if (item.text.equals("\n")) {
               atX = x;
               atY += fr.FONT_HEIGHT;
            } else {
               if (item.paragraph) {
                  atX = x;
                  atY = (int)((float)atY + (float)(fr.FONT_HEIGHT * 2) * prevScale);
               }

               prevScale = item.scale;
               String modifiers = "";
               modifiers = modifiers + TextFormatting.getValueByName(item.color);
               if (item.bold) {
                  modifiers = modifiers + TextFormatting.BOLD;
               }

               if (item.italic) {
                  modifiers = modifiers + TextFormatting.ITALIC;
               }

               if (item.underlined) {
                  modifiers = modifiers + TextFormatting.UNDERLINE;
               }

               if (item.strikethrough) {
                  modifiers = modifiers + TextFormatting.STRIKETHROUGH;
               }

               if (item.obfuscated) {
                  modifiers = modifiers + TextFormatting.OBFUSCATED;
               }

               String text = translateString(item.text);
               String[] split = cropStringBySize(text, modifiers, boxWidth, boxHeight - (atY - y), boxWidth - (atX - x), fr, item.scale);
               int box1X = atX;
               int box1Y = atY;
               int box2W = x + boxWidth;

               for(int i = 0; i < split.length; ++i) {
                  if (i == split.length - 1) {
                     box3X = atX;
                     box3Y = atY;
                  }

                  String s = split[i];
                  drawScaledString(fr, modifiers + s, (float)atX, (float)atY, 0, item.dropshadow, item.scale);
                  if (i < split.length - 1) {
                     atY += fr.FONT_HEIGHT;
                     atX = x;
                  }

                  if (i == 0) {
                     box2Y = atY;
                     if (atX == x) {
                        box1W = x + boxWidth;
                     } else {
                        box1W = atX;
                     }
                  }
               }

               int box2H = atY;
               atX = (int)((float)atX + (float)fr.getStringWidth(split[split.length - 1]) * item.scale);
               if (atX - x >= boxWidth) {
                  atX = x;
                  atY = (int)((float)atY + (float)fr.FONT_HEIGHT * item.scale);
               }

               int box3H = (int)((float)atY + (float)fr.FONT_HEIGHT * item.scale);
               if (item.tooltip != null && item.tooltip.length > 0 && (mouseX >= box1X && mouseX <= box1W && mouseY >= box1Y && mouseY <= box1H && box1X != box1W && box1Y != box1H || mouseX >= x && mouseX <= box2W && mouseY >= box2Y && mouseY <= box2H && x != box2W && box2Y != box2H || mouseX >= box3X && mouseX <= atX && mouseY >= box3Y && mouseY <= box3H && box3X != atX && box1Y != box3H)) {
                  tooltip.addAll(Arrays.asList(item.tooltip));
               }

               if (item.action != null && !item.action.isEmpty() && (mouseX >= box1X && mouseX <= box1W && mouseY >= box1Y && mouseY <= box1H && box1X != box1W && box1Y != box1H || mouseX >= x && mouseX <= box2W && mouseY >= box2Y && mouseY <= box2H && x != box2W && box2Y != box2H || mouseX >= box3X && mouseX <= atX && mouseY >= box3Y && mouseY <= box3H && box3X != atX && box1Y != box3H)) {
                  action = item.action;
               }

               if (atY >= y + boxHeight) {
                  fr.drawString("...", (float)atX, (float)atY, 0, item.dropshadow);
                  break;
               }

               y = atY;
            }
         }
      }

      if (GuiBook.debug && action != null && !action.isEmpty()) {
         tooltip.add("");
         tooltip.add(TextFormatting.GRAY + "Action: " + action);
      }

      return action;
   }

   public static String translateString(String s) {
      String loc;
      for(s = s.replace("$$(", "$\u0000(").replace(")$$", ")\u0000$"); s.contains("$(") && s.contains(")$") && s.indexOf("$(") < s.indexOf(")$"); s = s.replace("$(" + loc + ")$", I18n.format(loc, new Object[0]))) {
         loc = s.substring(s.indexOf("$(") + 2, s.indexOf(")$"));
      }

      if (s.indexOf("$(") > s.indexOf(")$") || s.contains(")$")) {
         Mantle.logger.error("[Books] [TextDataRenderer] Detected unbalanced localization symbols \"$(\" and \")$\" in string: \"" + s + "\".");
      }

      return s.replace("$\u0000(", "$(").replace(")\u0000$", ")$");
   }

   public static String[] cropStringBySize(String s, String modifiers, int width, int height, FontRenderer fr, float scale) {
      return cropStringBySize(s, modifiers, width, height, width, fr, scale);
   }

   public static String[] cropStringBySize(String s, String modifiers, int width, int height, int firstWidth, FontRenderer fr, float scale) {
      int curWidth = 0;
      int curHeight = (int)((float)fr.FONT_HEIGHT * scale);

      for(int i = 0; i < s.length(); ++i) {
         curWidth = (int)((float)curWidth + (float)fr.getStringWidth(modifiers + Character.toString(s.charAt(i))) * scale);
         if (s.charAt(i) == '\n' || curHeight == (int)((float)fr.FONT_HEIGHT * scale) && curWidth > firstWidth || curHeight != (int)((float)fr.FONT_HEIGHT * scale) && curWidth > width) {
            int oldI;
            for(oldI = i; i >= 0 && s.charAt(i) != ' '; --i) {
               ;
            }

            if (i <= 0) {
               i = oldI;
            }

            s = s.substring(0, i).trim() + "\r" + s.substring(i + (i == oldI ? 0 : 1)).trim();
            ++i;
            curWidth = 0;
            curHeight = (int)((float)curHeight + (float)fr.FONT_HEIGHT * scale);
            if (curHeight >= height) {
               return s.substring(0, i).trim().split("\r");
            }
         }
      }

      return s.split("\r");
   }

   public static void drawTooltip(List<String> textLines, int mouseX, int mouseY, FontRenderer font) {
      GuiUtils.drawHoveringText(textLines, mouseX, mouseY, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT, GuiBook.PAGE_WIDTH, font);
      RenderHelper.disableStandardItemLighting();
   }

   public static void drawScaledString(FontRenderer font, String text, float x, float y, int color, boolean dropShadow, float scale) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, 0.0F);
      GlStateManager.scale(scale, scale, 1.0F);
      font.drawString(text, 0.0F, 0.0F, color, dropShadow);
      GlStateManager.popMatrix();
   }

   private static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      float f = (float)(startColor >> 24 & 255) / 255.0F;
      float f1 = (float)(startColor >> 16 & 255) / 255.0F;
      float f2 = (float)(startColor >> 8 & 255) / 255.0F;
      float f3 = (float)(startColor & 255) / 255.0F;
      float f4 = (float)(endColor >> 24 & 255) / 255.0F;
      float f5 = (float)(endColor >> 16 & 255) / 255.0F;
      float f6 = (float)(endColor >> 8 & 255) / 255.0F;
      float f7 = (float)(endColor & 255) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertexBuffer = tessellator.getBuffer();
      vertexBuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vertexBuffer.pos((double)right, (double)top, 0.0D).color(f1, f2, f3, f).endVertex();
      vertexBuffer.pos((double)left, (double)top, 0.0D).color(f1, f2, f3, f).endVertex();
      vertexBuffer.pos((double)left, (double)bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
      vertexBuffer.pos((double)right, (double)bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }
}
