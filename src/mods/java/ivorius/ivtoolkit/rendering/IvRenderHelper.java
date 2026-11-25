package ivorius.ivtoolkit.rendering;

import ivorius.ivtoolkit.tools.BufferBuilderAccessor;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class IvRenderHelper {
   public IvRenderHelper() {
   }

   public static void drawRectFullScreen(Minecraft mc) {
      drawRectFullScreen(mc.displayWidth, mc.displayHeight);
   }

   public static void drawRectFullScreen(int screenWidth, int screenHeight) {
      BufferBuilder renderer = Tessellator.getInstance().getBuffer();
      renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      renderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
      renderer.pos(0.0D, (double)screenHeight, 0.0D).tex(0.0D, 0.0D).endVertex();
      renderer.pos((double)screenWidth, (double)screenHeight, 0.0D).tex(1.0D, 0.0D).endVertex();
      renderer.pos((double)screenWidth, 0.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
      Tessellator.getInstance().draw();
   }

   public static void renderLights(float ticks, int color, float alpha, int number) {
      float width = 2.5F;
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      BufferBuilder renderer = Tessellator.getInstance().getBuffer();
      float usedTicks = ticks / 200.0F;
      Random random = new Random(432L);
      GlStateManager.disableTexture2D();
      GlStateManager.shadeModel(7425);
      GlStateManager.enableBlend();
      OpenGlHelper.glBlendFunc(770, 1, 1, 0);
      GL11.glDisable(3008);
      GlStateManager.enableCull();
      GlStateManager.depthMask(false);
      GlStateManager.pushMatrix();

      for(int var7 = 0; (float)var7 < (float)number; ++var7) {
         float xLogFunc = ((float)var7 / (float)number * 28493.0F + ticks) / 10.0F % 20.0F;
         if (xLogFunc > 10.0F) {
            xLogFunc = 20.0F - xLogFunc;
         }

         float yLogFunc = 1.0F / (1.0F + (float)Math.pow(2.718280076980591D, (double)(-0.8F * xLogFunc)) * 99.0F);
         if (yLogFunc > 0.01F) {
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F + usedTicks * 90.0F, 0.0F, 0.0F, 1.0F);
            renderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            float var8 = random.nextFloat() * 20.0F + 5.0F;
            float var9 = random.nextFloat() * 2.0F + 1.0F;
            renderer.color(r, g, b, alpha * yLogFunc);
            renderer.pos(0.0D, 0.0D, 0.0D).endVertex();
            renderer.color(r, g, b, 0.0F);
            renderer.pos((double)(-width) * (double)var9, (double)var8, (double)(-0.5F * var9)).endVertex();
            renderer.pos((double)width * (double)var9, (double)var8, (double)(-0.5F * var9)).endVertex();
            renderer.pos(0.0D, (double)var8, (double)(1.0F * var9)).endVertex();
            renderer.pos((double)(-width) * (double)var9, (double)var8, (double)(-0.5F * var9)).endVertex();
            Tessellator.getInstance().draw();
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      GlStateManager.shadeModel(7424);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      GL11.glEnable(3008);
   }

   public static void renderParticle(BufferBuilder renderer, float time, float scale) {
      float f1 = ActiveRenderInfo.getRotationX();
      float f2 = ActiveRenderInfo.getRotationZ();
      float f3 = ActiveRenderInfo.getRotationYZ();
      float f4 = ActiveRenderInfo.getRotationXY();
      float f5 = ActiveRenderInfo.getRotationXZ();
      float f6 = 0.0F;
      float f7 = 1.0F;
      float f8 = 0.0F;
      float f9 = 1.0F;
      float f11 = 0.0F;
      float f12 = 0.0F;
      float f13 = 0.0F;
      renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      renderer.pos((double)(f11 - f1 * scale - f3 * scale), (double)(f12 - f5 * scale), (double)(f13 - f2 * scale - f4 * scale)).tex((double)f7, (double)f9).endVertex();
      renderer.pos((double)(f11 - f1 * scale + f3 * scale), (double)(f12 + f5 * scale), (double)(f13 - f2 * scale + f4 * scale)).tex((double)f7, (double)f8).endVertex();
      renderer.pos((double)(f11 + f1 * scale + f3 * scale), (double)(f12 + f5 * scale), (double)(f13 + f2 * scale + f4 * scale)).tex((double)f6, (double)f8).endVertex();
      renderer.pos((double)(f11 + f1 * scale - f3 * scale), (double)(f12 - f5 * scale), (double)(f13 + f2 * scale - f4 * scale)).tex((double)f6, (double)f9).endVertex();
      Tessellator.getInstance().draw();
   }

   public static void drawNormalCube(BufferBuilder tessellator, float size, float in, boolean lined) {
      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      } else {
         tessellator.begin(7, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)(-size * in), (double)(-size * in), (double)(-size)).endVertex();
      tessellator.pos((double)(size * in), (double)(-size * in), (double)(-size)).endVertex();
      tessellator.pos((double)(size * in), (double)(size * in), (double)(-size)).endVertex();
      tessellator.pos((double)(-size * in), (double)(size * in), (double)(-size)).endVertex();
      if (lined) {
         Tessellator.getInstance().draw();
      }

      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)(-size * in), (double)(-size * in), (double)size).endVertex();
      tessellator.pos((double)(-size * in), (double)(size * in), (double)size).endVertex();
      tessellator.pos((double)(size * in), (double)(size * in), (double)size).endVertex();
      tessellator.pos((double)(size * in), (double)(-size * in), (double)size).endVertex();
      if (lined) {
         Tessellator.getInstance().draw();
      }

      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)(-size), (double)(-size * in), (double)(-size * in)).endVertex();
      tessellator.pos((double)(-size), (double)(size * in), (double)(-size * in)).endVertex();
      tessellator.pos((double)(-size), (double)(size * in), (double)(size * in)).endVertex();
      tessellator.pos((double)(-size), (double)(-size * in), (double)(size * in)).endVertex();
      if (lined) {
         Tessellator.getInstance().draw();
      }

      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)size, (double)(-size * in), (double)(-size * in)).endVertex();
      tessellator.pos((double)size, (double)(-size * in), (double)(size * in)).endVertex();
      tessellator.pos((double)size, (double)(size * in), (double)(size * in)).endVertex();
      tessellator.pos((double)size, (double)(size * in), (double)(-size * in)).endVertex();
      if (lined) {
         Tessellator.getInstance().draw();
      }

      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)(-size * in), (double)size, (double)(-size * in)).endVertex();
      tessellator.pos((double)(size * in), (double)size, (double)(-size * in)).endVertex();
      tessellator.pos((double)(size * in), (double)size, (double)(size * in)).endVertex();
      tessellator.pos((double)(-size * in), (double)size, (double)(size * in)).endVertex();
      if (lined) {
         Tessellator.getInstance().draw();
      }

      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)(-size * in), (double)(-size), (double)(-size * in)).endVertex();
      tessellator.pos((double)(-size * in), (double)(-size), (double)(size * in)).endVertex();
      tessellator.pos((double)(size * in), (double)(-size), (double)(size * in)).endVertex();
      tessellator.pos((double)(size * in), (double)(-size), (double)(-size * in)).endVertex();
      Tessellator.getInstance().draw();
   }

   public static void drawCuboid(BufferBuilder tessellator, float sizeX, float sizeY, float sizeZ, float in, boolean lined) {
      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      } else {
         tessellator.begin(7, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)(-sizeX * in), (double)(-sizeY * in), (double)(-sizeZ)).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)(sizeY * in), (double)(-sizeZ)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(sizeY * in), (double)(-sizeZ)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(-sizeY * in), (double)(-sizeZ)).endVertex();
      if (lined) {
         Tessellator.getInstance().draw();
      }

      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)(-sizeX * in), (double)(-sizeY * in), (double)sizeZ).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(-sizeY * in), (double)sizeZ).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(sizeY * in), (double)sizeZ).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)(sizeY * in), (double)sizeZ).endVertex();
      if (lined) {
         Tessellator.getInstance().draw();
      }

      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)(-sizeX), (double)(-sizeY * in), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX), (double)(-sizeY * in), (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX), (double)(sizeY * in), (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX), (double)(sizeY * in), (double)(-sizeZ * in)).endVertex();
      if (lined) {
         Tessellator.getInstance().draw();
      }

      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)sizeX, (double)(-sizeY * in), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)sizeX, (double)(sizeY * in), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)sizeX, (double)(sizeY * in), (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)sizeX, (double)(-sizeY * in), (double)(sizeZ * in)).endVertex();
      if (lined) {
         Tessellator.getInstance().draw();
      }

      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)(-sizeX * in), (double)sizeY, (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)sizeY, (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)sizeY, (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)sizeY, (double)(-sizeZ * in)).endVertex();
      if (lined) {
         Tessellator.getInstance().draw();
      }

      if (lined) {
         tessellator.begin(3, DefaultVertexFormats.POSITION);
      }

      tessellator.pos((double)(-sizeX * in), (double)(-sizeY), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(-sizeY), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(-sizeY), (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)(-sizeY), (double)(sizeZ * in)).endVertex();
      Tessellator.getInstance().draw();
   }

   public static void renderCuboid(BufferBuilder tessellator, float sizeX, float sizeY, float sizeZ, float in) {
      tessellator.pos((double)(-sizeX * in), (double)(-sizeY * in), (double)(-sizeZ)).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)(sizeY * in), (double)(-sizeZ)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(sizeY * in), (double)(-sizeZ)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(-sizeY * in), (double)(-sizeZ)).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)(-sizeY * in), (double)sizeZ).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(-sizeY * in), (double)sizeZ).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(sizeY * in), (double)sizeZ).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)(sizeY * in), (double)sizeZ).endVertex();
      tessellator.pos((double)(-sizeX), (double)(-sizeY * in), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX), (double)(-sizeY * in), (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX), (double)(sizeY * in), (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX), (double)(sizeY * in), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)sizeX, (double)(-sizeY * in), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)sizeX, (double)(sizeY * in), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)sizeX, (double)(sizeY * in), (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)sizeX, (double)(-sizeY * in), (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)sizeY, (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)sizeY, (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)sizeY, (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)sizeY, (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)(-sizeY), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(-sizeY), (double)(-sizeZ * in)).endVertex();
      tessellator.pos((double)(sizeX * in), (double)(-sizeY), (double)(sizeZ * in)).endVertex();
      tessellator.pos((double)(-sizeX * in), (double)(-sizeY), (double)(sizeZ * in)).endVertex();
   }

   public static void drawModelCuboid(BufferBuilder renderer, float x, float y, float z, float sizeX, float sizeY, float sizeZ) {
      float tM = 0.0625F;
      float transX = (x + sizeX * 0.5F) * tM;
      float transY = (y + sizeY * 0.5F) * tM - 0.5F;
      float transZ = (z + sizeZ * 0.5F) * tM;
      BufferBuilderAccessor.addTranslation(renderer, (double)transX, (double)transY, (double)transZ);
      renderCuboid(renderer, sizeX * tM * 0.5F, sizeY * tM * 0.5F, sizeZ * tM * 0.5F, 1.0F);
      BufferBuilderAccessor.addTranslation(renderer, (double)(-transX), (double)(-transY), (double)(-transZ));
   }
}
