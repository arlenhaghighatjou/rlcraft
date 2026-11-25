package com.fantasticsource.mctools;

import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.Pair;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class Render {
   public static final byte SCALING_FULL = 0;
   public static final byte SCALING_MC_GUI = 1;
   private static Field activeRenderInfoViewportField;
   private static Field activeRenderInfoProjectionField;
   private static Field activeRenderInfoModelviewField;
   private static Field minecraftRenderPartialTicksPausedField;
   private static float fov;
   private static float fovMultiplier;

   public Render() {
   }

   public static void init() {
      try {
         activeRenderInfoViewportField = ReflectionTool.getField(ActiveRenderInfo.class, "VIEWPORT", "VIEWPORT");
         activeRenderInfoProjectionField = ReflectionTool.getField(ActiveRenderInfo.class, "PROJECTION", "PROJECTION");
         activeRenderInfoModelviewField = ReflectionTool.getField(ActiveRenderInfo.class, "MODELVIEW", "MODELVIEW");
         minecraftRenderPartialTicksPausedField = ReflectionTool.getField(Minecraft.class, "renderPartialTicksPaused", "renderPartialTicksPaused");
         MinecraftForge.EVENT_BUS.register(Render.class);
      } catch (Exception var1) {
         MCTools.crash(var1, false);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST,
      receiveCanceled = true
   )
   public static void drawHUD(Pre event) {
      if (event.getType() == ElementType.HOTBAR) {
         GlStateManager.pushMatrix();
         MinecraftForge.EVENT_BUS.post(new Render.RenderHUDEvent(event));
         GlStateManager.popMatrix();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void updateFOV(FOVModifier event) {
      fov = event.getFOV();
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void updateFOVMultiplier(FOVUpdateEvent event) {
      fovMultiplier = event.getNewfov();
   }

   public static int getPartialStringWidth(String beforePart, String part) {
      FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
      return fr.getStringWidth(beforePart + part) - fr.getStringWidth(beforePart);
   }

   public static void startOrtho() {
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0D, (double)Display.getWidth(), (double)Display.getHeight(), 0.0D, -1.0D, 1.0D);
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
   }

   public static void endOrtho() {
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
   }

   public static float getStoredVFOV() {
      return fov * fovMultiplier;
   }

   public static double getStoredHFOV(TrigLookupTable trigLookupTable) throws IllegalAccessException {
      return Tools.radtodeg(trigLookupTable.arctan(getStoredZNearWidth() * 0.5D / getStoredZNearDist())) * 2.0D;
   }

   public static double getPartialTick() throws IllegalAccessException {
      Minecraft mc = Minecraft.getMinecraft();
      return mc.isGamePaused() ? (double)((Float)minecraftRenderPartialTicksPausedField.get(mc)).floatValue() : (double)mc.getRenderPartialTicks();
   }

   public static Pair<Float, Float> getEntityXYInWindow(Entity entity) throws IllegalAccessException {
      return getEntityXYInWindow(entity, 0.0D, 0.0D, 0.0D);
   }

   public static Pair<Float, Float> getEntityXYInWindow(Entity entity, double xOffset, double yOffset, double zOffset) throws IllegalAccessException {
      double partialTick = getPartialTick();
      double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTick + xOffset;
      double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTick + yOffset;
      double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTick + zOffset;
      return get2DWindowCoordsFrom3DWorldCoords(x, y, z, partialTick);
   }

   public static Pair<Float, Float> get2DWindowCoordsFrom3DWorldCoords(double x, double y, double z) throws IllegalAccessException {
      return get2DWindowCoordsFrom3DWorldCoords(x, y, z, getPartialTick());
   }

   private static Pair<Float, Float> get2DWindowCoordsFrom3DWorldCoords(double x, double y, double z, double partialTick) throws IllegalAccessException {
      Entity viewEntity = Minecraft.getMinecraft().getRenderViewEntity();
      if (viewEntity == null) {
         viewEntity = Minecraft.getMinecraft().player;
      }

      double px = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTick;
      double py = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTick;
      double pz = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTick;
      FloatBuffer modelView = getStoredModelViewMatrix();
      FloatBuffer projection = getStoredProjectionMatrix();
      IntBuffer viewport = getStoredViewportMatrix();
      float[] v4f = new float[4];
      float[] tempV4f = new float[4];
      v4f[0] = (float)(x - px);
      v4f[1] = (float)(y - py);
      v4f[2] = (float)(z - pz);
      v4f[3] = 1.0F;
      multMatrix(modelView, v4f, tempV4f);
      multMatrix(projection, tempV4f, v4f);
      if ((double)v4f[3] == 0.0D) {
         return null;
      } else {
         v4f[3] = 1.0F / v4f[3] * 0.5F;
         boolean behind = v4f[3] < 0.0F;
         if (behind) {
            v4f[0] = -v4f[0];
            v4f[1] = -v4f[1];
         }

         v4f[0] *= v4f[3];
         v4f[1] *= v4f[3];
         float scaleFactor = behind ? Math.abs(v4f[0] * 2.0F) : Tools.max(Math.abs(v4f[0] * 2.0F), Math.abs(v4f[1] * 2.0F));
         if (behind || scaleFactor > 1.0F) {
            v4f[0] /= scaleFactor;
            v4f[1] /= scaleFactor;
         }

         float xx = (0.5F + v4f[0]) * (float)viewport.get(2) + (float)viewport.get(0);
         float yy = (0.5F - v4f[1]) * (float)viewport.get(3) + (float)viewport.get(1);
         return new Pair<Float, Float>(xx, yy);
      }
   }

   private static void multMatrix(FloatBuffer m, float[] in, float[] out) {
      for(int i = 0; i < 4; ++i) {
         out[i] = in[0] * m.get(i) + in[1] * m.get(i + 4) + in[2] * m.get(i + 8) + in[3] * m.get(i + 12);
      }

   }

   public static double getCurrentZNearDist() {
      FloatBuffer projection = getCurrentProjectionMatrix();
      return (double)(2.0F * projection.get(11) / (2.0F * projection.get(10) - 2.0F));
   }

   public static double getStoredZNearDist() throws IllegalAccessException {
      FloatBuffer projection = getStoredProjectionMatrix();
      return (double)(2.0F * projection.get(11) / (2.0F * projection.get(10) - 2.0F));
   }

   public static double getCurrentZNearWidth() {
      return getCurrentZNearDist() * 2.0D / (double)getCurrentProjectionMatrix().get(0);
   }

   public static double getStoredZNearWidth() throws IllegalAccessException {
      return getStoredZNearDist() * 2.0D / (double)getStoredProjectionMatrix().get(0);
   }

   public static double getCurrentZNearHeight() {
      return getCurrentZNearDist() * 2.0D / (double)getCurrentProjectionMatrix().get(5);
   }

   public static double getStoredZNearHeight() throws IllegalAccessException {
      return getStoredZNearDist() * 2.0D / (double)getStoredProjectionMatrix().get(5);
   }

   public static int getCurrentViewportWidth() {
      return getCurrentViewportMatrix().get(2);
   }

   public static int getStoredViewportWidth() throws IllegalAccessException {
      return getStoredViewportMatrix().get(2);
   }

   public static int getCurrentViewportHeight() {
      return getCurrentViewportMatrix().get(3);
   }

   public static int getStoredViewportHeight() throws IllegalAccessException {
      return getStoredViewportMatrix().get(3);
   }

   public static IntBuffer getCurrentViewportMatrix() {
      IntBuffer result = ByteBuffer.allocateDirect(64).asIntBuffer();
      GlStateManager.glGetInteger(2983, result);
      return result;
   }

   public static IntBuffer getStoredViewportMatrix() throws IllegalAccessException {
      return ((IntBuffer)activeRenderInfoViewportField.get((Object)null)).duplicate();
   }

   public static FloatBuffer getCurrentProjectionMatrix() {
      FloatBuffer result = ByteBuffer.allocateDirect(64).asFloatBuffer();
      GlStateManager.getFloat(2983, result);
      return result;
   }

   public static FloatBuffer getStoredProjectionMatrix() throws IllegalAccessException {
      return ((FloatBuffer)activeRenderInfoProjectionField.get((Object)null)).duplicate();
   }

   public static void setProjectionMatrix(FloatBuffer matrix) {
      GlStateManager.matrixMode(5889);
      GL11.glLoadMatrix(matrix);
   }

   public static FloatBuffer getCurrentModelViewMatrix() {
      FloatBuffer result = ByteBuffer.allocateDirect(64).asFloatBuffer();
      GlStateManager.getFloat(2982, result);
      return result;
   }

   public static FloatBuffer getStoredModelViewMatrix() throws IllegalAccessException {
      return ((FloatBuffer)activeRenderInfoModelviewField.get((Object)null)).duplicate();
   }

   public static void setModelViewMatrix(FloatBuffer matrix) {
      GlStateManager.matrixMode(5888);
      GL11.glLoadMatrix(matrix);
   }

   public static Vec3d getCameraPosition() {
      Entity viewEntity = Minecraft.getMinecraft().getRenderViewEntity();
      if (viewEntity == null) {
         viewEntity = Minecraft.getMinecraft().player;
      }

      return viewEntity.getPositionVector().add(ActiveRenderInfo.getCameraPosition());
   }

   public static class RenderHUDEvent extends Event {
      Pre parentEvent;
      byte scalingMode = 1;
      ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
      int width;
      int height;

      public RenderHUDEvent(Pre parentEvent) {
         this.width = this.sr.getScaledWidth();
         this.height = this.sr.getScaledHeight();
         this.parentEvent = parentEvent;
      }

      public Pre getParentEvent() {
         return this.parentEvent;
      }

      public void setScalingMode(byte scalingMode) throws IllegalAccessException {
         if (this.scalingMode != scalingMode) {
            double xRatio = (double)this.width;
            double yRatio = (double)this.height;
            switch(scalingMode) {
            case 0:
               this.width = Render.getStoredViewportWidth();
               this.height = Render.getStoredViewportHeight();
               break;
            case 1:
               this.width = this.sr.getScaledWidth();
               this.height = this.sr.getScaledHeight();
               break;
            default:
               return;
            }

            this.scalingMode = scalingMode;
            xRatio = xRatio / (double)this.width;
            yRatio = yRatio / (double)this.height;
            GlStateManager.scale(xRatio, yRatio, 1.0D);
         }
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }
   }
}
