package bqtweaker.client.util;

import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.core.BetterQuesting;
import goblinbob.mobends.core.util.BenderHelper;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.LogicOp;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.client.event.RenderTooltipEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderUtilsOverride {
   public static final String REGEX_NUMBER = "[^\\.0123456789-]";
   private static final IGuiColor STENCIL_COLOR = new GuiColorStatic(0, 0, 0, 255);
   private static int stencilDepth = 0;

   public RenderUtilsOverride() {
   }

   public static void RenderItemStack(Minecraft mc, ItemStack stack, int x, int y, String text) {
      RenderItemStack(mc, stack, x, y, text, Color.WHITE.getRGB());
   }

   public static void RenderItemStack(Minecraft mc, ItemStack stack, int x, int y, String text, Color color) {
      RenderItemStack(mc, stack, x, y, text, color.getRGB());
   }

   public static void RenderItemStack(Minecraft mc, ItemStack stack, int x, int y, String text, int color) {
      RenderItemStack(mc, stack, x, y, 16.0F, text, color);
   }

   public static void RenderItemStack(Minecraft mc, ItemStack stack, int x, int y, float z, String text, int color) {
      if (stack != null && !stack.isEmpty()) {
         GlStateManager.pushMatrix();
         RenderItem itemRender = mc.getRenderItem();
         float preZ = itemRender.zLevel;
         float r = (float)(color >> 16 & 255) / 255.0F;
         float g = (float)(color >> 8 & 255) / 255.0F;
         float b = (float)(color & 255) / 255.0F;
         GlStateManager.color(r, g, b);
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableDepth();
         GlStateManager.translate(0.0F, 0.0F, z);
         itemRender.zLevel = -150.0F;
         FontRenderer font = stack.getItem().getFontRenderer(stack);
         if (font == null) {
            font = mc.fontRenderer;
         }

         try {
            itemRender.renderItemAndEffectIntoGUI(stack, x, y);
            if (stack.getCount() != 1 || text != null) {
               GlStateManager.pushMatrix();
               int w = getStringWidth(text, font);
               float s = 1.0F;
               float tx;
               float ty;
               if (w > 17) {
                  s = 17.0F / (float)w;
                  tx = 0.0F;
                  ty = 17.0F - (float)font.FONT_HEIGHT * s;
               } else {
                  tx = (float)(17 - w);
                  ty = (float)(18 - font.FONT_HEIGHT);
               }

               GlStateManager.translate((float)x + tx, (float)y + ty, 0.0F);
               GlStateManager.scale(s, s, 1.0F);
               GlStateManager.disableLighting();
               GlStateManager.disableDepth();
               GlStateManager.disableBlend();
               font.drawString(text, 0.0F, 0.0F, 16777215, true);
               GlStateManager.enableLighting();
               GlStateManager.enableDepth();
               GlStateManager.enableBlend();
               GlStateManager.popMatrix();
            }

            itemRender.renderItemOverlayIntoGUI(font, stack, x, y, "");
         } catch (Exception var17) {
            BetterQuesting.logger.warn("Unabled to render item " + stack, var17);
         }

         GlStateManager.disableDepth();
         RenderHelper.disableStandardItemLighting();
         itemRender.zLevel = preZ;
         GlStateManager.popMatrix();
      }
   }

   public static void RenderEntity(int posX, int posY, int scale, float rotation, float pitch, Entity entity) {
      RenderEntity((float)posX, (float)posY, 64.0F, scale, rotation, pitch, entity);
   }

   public static void RenderEntity(float posX, float posY, float posZ, int scale, float rotation, float pitch, Entity entity) {
      try {
         GlStateManager.enableColorMaterial();
         GlStateManager.pushMatrix();
         GlStateManager.enableDepth();
         GlStateManager.translate(posX, posY, posZ);
         GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
         GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
         float f3 = entity.rotationYaw;
         float f4 = entity.rotationPitch;
         float f5 = entity.prevRotationYaw;
         float f6 = entity.prevRotationPitch;
         entity.rotationYaw = 0.0F;
         entity.rotationPitch = 0.0F;
         entity.prevRotationYaw = 0.0F;
         entity.prevRotationPitch = 0.0F;
         EntityLivingBase livingBase = entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
         float f7 = livingBase == null ? 0.0F : livingBase.renderYawOffset;
         float f8 = livingBase == null ? 0.0F : livingBase.rotationYawHead;
         float f9 = livingBase == null ? 0.0F : livingBase.prevRotationYawHead;
         if (livingBase != null) {
            livingBase.renderYawOffset = 0.0F;
            livingBase.rotationYawHead = 0.0F;
            livingBase.prevRotationYawHead = 0.0F;
         }

         RenderHelper.enableStandardItemLighting();
         RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
         rendermanager.setPlayerViewY(180.0F);
         rendermanager.renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);

         try {
            if (rendermanager.getEntityRenderObject(entity) != null) {
               RenderLivingBase<? extends EntityLivingBase> render = (RenderLivingBase)rendermanager.getEntityRenderObject(entity);
               BenderHelper.getData((EntityLivingBase)entity, render).initModelPose();
            }
         } catch (Exception var17) {
            ;
         }

         entity.rotationYaw = f3;
         entity.rotationPitch = f4;
         entity.prevRotationYaw = f5;
         entity.prevRotationPitch = f6;
         if (livingBase != null) {
            livingBase.renderYawOffset = f7;
            livingBase.rotationYawHead = f8;
            livingBase.prevRotationYawHead = f9;
         }

         GlStateManager.disableDepth();
         GlStateManager.popMatrix();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
         OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.disableTexture2D();
         OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.disableColorMaterial();
      } catch (Exception var18) {
         ;
      }

   }

   public static void DrawLine(int x1, int y1, int x2, int y2, float width, int color) {
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      GlStateManager.pushMatrix();
      GlStateManager.disableTexture2D();
      GlStateManager.color(r, g, b, 1.0F);
      GL11.glLineWidth(width);
      GL11.glBegin(1);
      GL11.glVertex2f((float)x1, (float)y1);
      GL11.glVertex2f((float)x2, (float)y2);
      GL11.glEnd();
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public static void drawSplitString(FontRenderer renderer, String string, int x, int y, int width, int color, boolean shadow) {
      drawSplitString(renderer, string, x, y, width, color, shadow, 0, splitString(string, width, renderer).size() - 1);
   }

   public static void drawSplitString(FontRenderer renderer, String string, int x, int y, int width, int color, boolean shadow, int start, int end) {
      drawHighlightedSplitString(renderer, string, x, y, width, color, shadow, start, end, 0, 0, 0);
   }

   public static void drawHighlightedSplitString(FontRenderer renderer, String string, int x, int y, int width, int color, boolean shadow, int highlightColor, int highlightStart, int highlightEnd) {
      drawHighlightedSplitString(renderer, string, x, y, width, color, shadow, 0, splitString(string, width, renderer).size() - 1, highlightColor, highlightStart, highlightEnd);
   }

   public static void drawHighlightedSplitString(FontRenderer renderer, String string, int x, int y, int width, int color, boolean shadow, int start, int end, int highlightColor, int highlightStart, int highlightEnd) {
      if (renderer != null && string != null && string.length() > 0 && start <= end) {
         string = string.replaceAll("\r", "");
         List<String> list = splitString(string, width, renderer);
         List<String> noFormat = splitStringWithoutFormat(string, width, renderer);
         if (list.size() == noFormat.size()) {
            int hlStart = Math.min(highlightStart, highlightEnd);
            int hlEnd = Math.max(highlightStart, highlightEnd);
            int idxStart = 0;

            for(int i = 0; i < start && i < noFormat.size(); ++i) {
               idxStart += ((String)noFormat.get(i)).length();
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            for(int i = start; i <= end; ++i) {
               if (i >= 0 && i < list.size()) {
                  renderer.drawString((String)list.get(i), (float)x, (float)(y + renderer.FONT_HEIGHT * (i - start)), color, shadow);
                  int lineSize = ((String)noFormat.get(i)).length();
                  int idxEnd = idxStart + lineSize;
                  int i1 = Math.max(idxStart, hlStart) - idxStart;
                  int i2 = Math.min(idxEnd, hlEnd) - idxStart;
                  if (i1 != i2 && i1 >= 0 && i2 >= 0 && i1 <= lineSize && i2 <= lineSize) {
                     String lastFormat = FontRenderer.getFormatFromString((String)list.get(i));
                     int x1 = getStringWidth(lastFormat + ((String)noFormat.get(i)).substring(0, i1), renderer);
                     int x2 = getStringWidth(lastFormat + ((String)noFormat.get(i)).substring(0, i2), renderer);
                     drawHighlightBox(x + x1, y + renderer.FONT_HEIGHT * (i - start), x + x2, y + renderer.FONT_HEIGHT * (i - start) + renderer.FONT_HEIGHT, highlightColor);
                  }

                  idxStart = idxEnd;
               }
            }

         }
      }
   }

   public static void drawHighlightedString(FontRenderer renderer, String string, int x, int y, int color, boolean shadow, int highlightColor, int highlightStart, int highlightEnd) {
      if (renderer != null && string != null && string.length() > 0) {
         renderer.drawString(string, (float)x, (float)y, color, shadow);
         int hlStart = Math.min(highlightStart, highlightEnd);
         int hlEnd = Math.max(highlightStart, highlightEnd);
         int size = string.length();
         int i1 = MathHelper.clamp(hlStart, 0, size);
         int i2 = MathHelper.clamp(hlEnd, 0, size);
         if (i1 != i2) {
            int x1 = getStringWidth(string.substring(0, i1), renderer);
            int x2 = getStringWidth(string.substring(0, i2), renderer);
            drawHighlightBox(x + x1, y, x + x2, y + renderer.FONT_HEIGHT, highlightColor);
         }

      }
   }

   public static void drawHighlightBox(IGuiRect rect, IGuiColor color) {
      drawHighlightBox(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), color.getRGB());
   }

   public static void drawHighlightBox(int left, int top, int right, int bottom, int color) {
      if (left < right) {
         int i = left;
         left = right;
         right = i;
      }

      if (top < bottom) {
         int j = top;
         top = bottom;
         bottom = j;
      }

      float f3 = (float)(color >> 24 & 255) / 255.0F;
      float f = (float)(color >> 16 & 255) / 255.0F;
      float f1 = (float)(color >> 8 & 255) / 255.0F;
      float f2 = (float)(color & 255) / 255.0F;
      GlStateManager.pushMatrix();
      GL11.glDisable(3553);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferbuilder = tessellator.getBuffer();
      GlStateManager.color(f, f1, f2, f3);
      GlStateManager.disableTexture2D();
      GlStateManager.enableColorLogic();
      GlStateManager.colorLogicOp(LogicOp.OR_REVERSE);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
      bufferbuilder.pos((double)left, (double)bottom, 0.0D).endVertex();
      bufferbuilder.pos((double)right, (double)bottom, 0.0D).endVertex();
      bufferbuilder.pos((double)right, (double)top, 0.0D).endVertex();
      bufferbuilder.pos((double)left, (double)top, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.disableColorLogic();
      GlStateManager.enableTexture2D();
      GL11.glEnable(3553);
      GlStateManager.popMatrix();
   }

   public static void drawColoredRect(IGuiRect rect, IGuiColor color) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexbuffer = tessellator.getBuffer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      color.applyGlColor();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
      vertexbuffer.pos((double)rect.getX(), (double)rect.getY() + (double)rect.getHeight(), 0.0D).endVertex();
      vertexbuffer.pos((double)rect.getX() + (double)rect.getWidth(), (double)rect.getY() + (double)rect.getHeight(), 0.0D).endVertex();
      vertexbuffer.pos((double)rect.getX() + (double)rect.getWidth(), (double)rect.getY(), 0.0D).endVertex();
      vertexbuffer.pos((double)rect.getX(), (double)rect.getY(), 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void startScissor(IGuiRect rect) {
      if (stencilDepth >= 255) {
         throw new IndexOutOfBoundsException("Exceeded the maximum number of nested stencils (255)");
      } else {
         if (stencilDepth == 0) {
            GL11.glEnable(2960);
            GL11.glStencilMask(255);
            GL11.glClear(1024);
         }

         GL11.glStencilFunc(513, stencilDepth, 255);
         GL11.glStencilOp(7682, 7680, 7680);
         GL11.glStencilMask(255);
         GL11.glColorMask(false, false, false, false);
         GL11.glDepthMask(false);
         drawColoredRect(rect, STENCIL_COLOR);
         GL11.glStencilMask(0);
         GL11.glStencilFunc(514, stencilDepth + 1, 255);
         GL11.glColorMask(true, true, true, true);
         GL11.glDepthMask(true);
         ++stencilDepth;
      }
   }

   private static void fillScreen() {
      int w = Minecraft.getMinecraft().displayWidth;
      int h = Minecraft.getMinecraft().displayHeight;
      GL11.glPushAttrib(265073);
      GL11.glDisable(2929);
      GL11.glDisable(2896);
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, (double)w, (double)h, 0.0D, -1.0D, 1.0D);
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      drawColoredRect(new GuiRectangle(0, 0, w, h), STENCIL_COLOR);
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
   }

   public static void endScissor() {
      --stencilDepth;
      if (stencilDepth < 0) {
         throw new IndexOutOfBoundsException("No stencil to end");
      } else {
         if (stencilDepth == 0) {
            GL11.glStencilMask(255);
            GL11.glClear(1024);
            GL11.glStencilFunc(519, 1, 255);
            GL11.glStencilOp(7680, 7680, 7680);
            GL11.glStencilMask(0);
            GL11.glDisable(2960);
         } else {
            GL11.glStencilFunc(515, stencilDepth, 255);
            GL11.glStencilOp(7683, 7680, 7680);
            GL11.glStencilMask(255);
            GL11.glColorMask(false, false, false, false);
            GL11.glDepthMask(false);
            fillScreen();
            GL11.glColorMask(true, true, true, true);
            GL11.glDepthMask(true);
            GL11.glStencilFunc(514, stencilDepth, 255);
            GL11.glStencilMask(0);
         }

      }
   }

   public static List<String> splitStringWithoutFormat(String str, int wrapWidth, FontRenderer font) {
      List<String> list = new ArrayList();
      String lastFormat = "";
      String temp = str;

      while(true) {
         int i = sizeStringToWidth(lastFormat + temp, wrapWidth, font);
         i = i - lastFormat.length();
         if (temp.length() <= i) {
            list.add(temp);
            break;
         }

         String s = temp.substring(0, i);
         char c0 = temp.charAt(i);
         boolean flag = c0 == ' ' || c0 == '\n';
         lastFormat = FontRenderer.getFormatFromString(lastFormat + s);
         temp = temp.substring(i + (flag ? 1 : 0));
         list.add(s + (flag ? "\n" : ""));
         if (temp.length() <= 0 && !flag) {
            break;
         }
      }

      return list;
   }

   public static List<String> splitString(String str, int wrapWidth, FontRenderer font) {
      List<String> list = new ArrayList();
      String temp = str;

      while(true) {
         int i = sizeStringToWidth(temp, wrapWidth, font);
         if (temp.length() <= i) {
            list.add(temp);
            break;
         }

         String s = temp.substring(0, i);
         char c0 = temp.charAt(i);
         boolean flag = c0 == ' ' || c0 == '\n';
         temp = FontRenderer.getFormatFromString(s) + temp.substring(i + (flag ? 1 : 0));
         list.add(s);
         if (temp.length() <= 0 && !flag) {
            break;
         }
      }

      return list;
   }

   public static int getCursorPos(String text, int x, FontRenderer font) {
      if (text.length() <= 0) {
         return 0;
      } else {
         int i;
         for(i = 0; i < text.length() && getStringWidth(text.substring(0, i + 1), font) <= x; ++i) {
            ;
         }

         return i - 1 >= 0 && text.charAt(i - 1) == '\n' ? i - 1 : i;
      }
   }

   public static int getCursorPos(String text, int x, int y, int width, FontRenderer font) {
      List<String> tLines = splitStringWithoutFormat(text, width, font);
      if (tLines.size() <= 0) {
         return 0;
      } else {
         int row = MathHelper.clamp(y / font.FONT_HEIGHT, 0, tLines.size() - 1);
         String lastFormat = "";
         int idx = 0;

         for(int i = 0; i < row; ++i) {
            String line = (String)tLines.get(i);
            idx += line.length();
            lastFormat = FontRenderer.getFormatFromString(lastFormat + line);
         }

         return idx + getCursorPos(lastFormat + (String)tLines.get(row), x, font) - lastFormat.length();
      }
   }

   private static int sizeStringToWidth(String str, int wrapWidth, FontRenderer font) {
      int i = str.length();
      int j = 0;
      int k = 0;
      int l = -1;

      for(boolean flag = false; k < i; ++k) {
         char c0 = str.charAt(k);
         switch(c0) {
         case '\n':
            --k;
            break;
         case ' ':
            l = k;
         default:
            j += font.getCharWidth(c0);
            if (flag) {
               ++j;
            }
            break;
         case '\u00a7':
            if (k < i - 1) {
               ++k;
               char c1 = str.charAt(k);
               if (c1 != 'l' && c1 != 'L') {
                  if (c1 == 'r' || c1 == 'R' || isFormatColor(c1)) {
                     flag = false;
                  }
               } else {
                  flag = true;
               }
            }
         }

         if (c0 == '\n') {
            ++k;
            l = k;
            break;
         }

         if (j > wrapWidth) {
            break;
         }
      }

      return k != i && l != -1 && l < k ? l : k;
   }

   private static boolean isFormatColor(char colorChar) {
      return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
   }

   public static float lerpFloat(float f1, float f2, float blend) {
      return f2 * blend + f1 * (1.0F - blend);
   }

   public static double lerpDouble(double d1, double d2, double blend) {
      return d2 * blend + d1 * (1.0D - blend);
   }

   public static int lerpRGB(int c1, int c2, float blend) {
      float a1 = (float)(c1 >> 24 & 255);
      float r1 = (float)(c1 >> 16 & 255);
      float g1 = (float)(c1 >> 8 & 255);
      float b1 = (float)(c1 & 255);
      float a2 = (float)(c2 >> 24 & 255);
      float r2 = (float)(c2 >> 16 & 255);
      float g2 = (float)(c2 >> 8 & 255);
      float b2 = (float)(c2 & 255);
      int a3 = (int)lerpFloat(a1, a2, blend);
      int r3 = (int)lerpFloat(r1, r2, blend);
      int g3 = (int)lerpFloat(g1, g2, blend);
      int b3 = (int)lerpFloat(b1, b2, blend);
      return (a3 << 24) + (r3 << 16) + (g3 << 8) + b3;
   }

   public static void drawHoveringText(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
      drawHoveringText(ItemStack.EMPTY, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
   }

   public static void drawHoveringText(@Nonnull ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
      if (textLines != null && !textLines.isEmpty()) {
         Pre event = new Pre(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
         if (!MinecraftForge.EVENT_BUS.post(event)) {
            mouseX = event.getX();
            mouseY = event.getY();
            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();
            maxTextWidth = event.getMaxWidth();
            font = event.getFontRenderer();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 32.0F);
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int tooltipTextWidth = 0;

            for(String textLine : textLines) {
               int textLineWidth = getStringWidth(textLine, font);
               if (textLineWidth > tooltipTextWidth) {
                  tooltipTextWidth = textLineWidth;
               }
            }

            boolean needsWrap = false;
            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
               tooltipX = mouseX - 16 - tooltipTextWidth;
               if (tooltipX < 4) {
                  if (mouseX > screenWidth / 2) {
                     tooltipTextWidth = mouseX - 12 - 8;
                  } else {
                     tooltipTextWidth = screenWidth - 16 - mouseX;
                  }

                  needsWrap = true;
               }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
               tooltipTextWidth = maxTextWidth;
               needsWrap = true;
            }

            if (needsWrap) {
               int wrappedTooltipWidth = 0;
               List<String> wrappedTextLines = new ArrayList();

               for(int i = 0; i < textLines.size(); ++i) {
                  String textLine = (String)textLines.get(i);
                  List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                  if (i == 0) {
                     titleLinesCount = wrappedLine.size();
                  }

                  for(String line : wrappedLine) {
                     int lineWidth = getStringWidth(line, font);
                     if (lineWidth > wrappedTooltipWidth) {
                        wrappedTooltipWidth = lineWidth;
                     }

                     wrappedTextLines.add(line);
                  }
               }

               tooltipTextWidth = wrappedTooltipWidth;
               textLines = wrappedTextLines;
               if (mouseX > screenWidth / 2) {
                  tooltipX = mouseX - 16 - wrappedTooltipWidth;
               } else {
                  tooltipX = mouseX + 12;
               }
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;
            if (textLines.size() > 1) {
               tooltipHeight += (textLines.size() - 1) * 10;
               if (textLines.size() > titleLinesCount) {
                  tooltipHeight += 2;
               }
            }

            if (tooltipY < 4) {
               tooltipY = 4;
            } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
               tooltipY = screenHeight - tooltipHeight - 4;
            }

            PresetTexture.TOOLTIP_BG.getTexture().drawTexture(tooltipX - 4, tooltipY - 4, tooltipTextWidth + 8, tooltipHeight + 8, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, 0.0F, 0.1F);

            for(int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
               String line = (String)textLines.get(lineNumber);
               font.drawStringWithShadow(line, (float)tooltipX, (float)tooltipY, -1);
               if (lineNumber + 1 == titleLinesCount) {
                  tooltipY += 2;
               }

               tooltipY += 10;
            }

            MinecraftForge.EVENT_BUS.post(new PostText(stack, textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
            GlStateManager.enableLighting();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.popMatrix();
         }
      }
   }

   public static int getStringWidth(String text, FontRenderer font) {
      if (text != null && text.length() != 0) {
         int maxWidth = 0;
         int curLineWidth = 0;
         boolean bold = false;

         for(int j = 0; j < text.length(); ++j) {
            char c0 = text.charAt(j);
            int k = font.getCharWidth(c0);
            if (k < 0 && j < text.length() - 1) {
               ++j;
               c0 = text.charAt(j);
               if (c0 != 'l' && c0 != 'L') {
                  int ci = "0123456789abcdefklmnor".indexOf(String.valueOf(c0).toLowerCase(Locale.ROOT).charAt(0));
                  if (ci < 16 || ci == 21) {
                     bold = false;
                  }
               } else {
                  bold = true;
               }

               k = 0;
            }

            curLineWidth += k;
            if (bold && k > 0) {
               ++curLineWidth;
            }

            if (c0 == '\n') {
               maxWidth = Math.max(maxWidth, curLineWidth);
               curLineWidth = 0;
            }
         }

         return Math.max(maxWidth, curLineWidth);
      } else {
         return 0;
      }
   }
}
