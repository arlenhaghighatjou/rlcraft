package com.creativemd.creativecore.client.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderHelper3D {
   public static Minecraft mc = Minecraft.getMinecraft();

   public RenderHelper3D() {
   }

   public static void renderBlock(double x, double y, double z, double width, double height, double length, double rotateX, double rotateY, double rotateZ, double red, double green, double blue, double alpha) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.rotate((float)rotateX, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate((float)rotateY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((float)rotateZ, 0.0F, 0.0F, 1.0F);
      GlStateManager.scale(width, height, length);
      GlStateManager.color((float)red, (float)green, (float)blue, (float)alpha);
      GlStateManager.glBegin(9);
      GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(0.5F, 0.5F, -0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, -0.5F);
      GlStateManager.glEnd();
      GL11.glBegin(9);
      GlStateManager.glNormal3f(0.0F, 0.0F, 1.0F);
      GlStateManager.glVertex3f(0.5F, -0.5F, 0.5F);
      GlStateManager.glVertex3f(0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, 0.5F);
      GlStateManager.glEnd();
      GL11.glBegin(9);
      GlStateManager.glNormal3f(1.0F, 0.0F, 0.0F);
      GlStateManager.glVertex3f(0.5F, 0.5F, -0.5F);
      GlStateManager.glVertex3f(0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(0.5F, -0.5F, 0.5F);
      GlStateManager.glVertex3f(0.5F, -0.5F, -0.5F);
      GlStateManager.glEnd();
      GL11.glBegin(9);
      GlStateManager.glNormal3f(-1.0F, 0.0F, 0.0F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, -0.5F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, -0.5F);
      GlStateManager.glEnd();
      GL11.glBegin(9);
      GlStateManager.glNormal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.glVertex3f(0.5F, -0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, -0.5F);
      GlStateManager.glVertex3f(0.5F, -0.5F, -0.5F);
      GlStateManager.glEnd();
      GL11.glBegin(9);
      GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F);
      GlStateManager.glVertex3f(0.5F, 0.5F, -0.5F);
      GlStateManager.glVertex3f(0.5F, -0.5F, -0.5F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, -0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, -0.5F);
      GlStateManager.glEnd();
      GlStateManager.popMatrix();
   }

   public static void applyDirection(EnumFacing direction) {
      int rotation = 0;
      switch(direction) {
      case EAST:
         rotation = 0;
         break;
      case NORTH:
         rotation = 90;
         break;
      case SOUTH:
         rotation = 270;
         break;
      case WEST:
         rotation = 180;
         break;
      case UP:
         GL11.glRotated(90.0D, 1.0D, 0.0D, 0.0D);
         GL11.glRotated(-90.0D, 0.0D, 0.0D, 1.0D);
         break;
      case DOWN:
         GL11.glRotated(-90.0D, 1.0D, 0.0D, 0.0D);
         GL11.glRotated(-90.0D, 0.0D, 0.0D, 1.0D);
      }

      GL11.glRotated((double)rotation, 0.0D, 1.0D, 0.0D);
   }
}
