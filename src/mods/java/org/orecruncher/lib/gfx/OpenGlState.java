package org.orecruncher.lib.gfx;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import org.lwjgl.opengl.GL11;

public final class OpenGlState {
   private final boolean enableBlend = isSet(3042);
   private final int blendSource = getInteger(3041);
   private final int blendDest = getInteger(3040);
   private final int blendEquation = getInteger(32777);
   private final boolean enableAlphaTest = isSet(3008);
   private final int alphaTestFunc = getInteger(3009);
   private final float alphaTestRef = getFloat(3010);
   private final boolean depthTest = isSet(2929);
   private final int depthFunc = getInteger(2932);
   private final boolean cull = isSet(2884);
   private CullFace cullMode;
   private final boolean lighting;
   private final boolean depthMask;
   private final boolean normal;
   private final boolean rescaleNormal;
   private final boolean texture2D;

   private static int getInteger(int parm) {
      return GL11.glGetInteger(parm);
   }

   private static float getFloat(int parm) {
      return GL11.glGetFloat(parm);
   }

   private static boolean isSet(int parm) {
      return getInteger(parm) == 1;
   }

   private OpenGlState() {
      switch(getInteger(2885)) {
      case 1028:
      case 1030:
      case 1031:
      default:
         this.cullMode = CullFace.FRONT;
         break;
      case 1029:
         this.cullMode = CullFace.BACK;
         break;
      case 1032:
         this.cullMode = CullFace.FRONT_AND_BACK;
      }

      this.lighting = isSet(2896);
      this.depthMask = isSet(2930);
      this.normal = isSet(2977);
      this.rescaleNormal = isSet(32826);
      this.texture2D = isSet(3553);
      GlStateManager.pushMatrix();
   }

   public static OpenGlState push() {
      return new OpenGlState();
   }

   public static void pop(OpenGlState state) {
      GlStateManager.popMatrix();
      if (state.enableBlend) {
         GlStateManager.enableBlend();
      } else {
         GlStateManager.disableBlend();
      }

      GlStateManager.blendFunc(state.blendSource, state.blendDest);
      GlStateManager.glBlendEquation(state.blendEquation);
      if (state.enableAlphaTest) {
         GlStateManager.enableAlpha();
      } else {
         GlStateManager.disableAlpha();
      }

      GlStateManager.alphaFunc(state.alphaTestFunc, state.alphaTestRef);
      if (state.depthTest) {
         GlStateManager.enableDepth();
      } else {
         GlStateManager.disableDepth();
      }

      GlStateManager.depthFunc(state.depthFunc);
      if (state.cull) {
         GlStateManager.enableCull();
      } else {
         GlStateManager.disableCull();
      }

      GlStateManager.cullFace(state.cullMode);
      if (state.lighting) {
         GlStateManager.enableLighting();
      } else {
         GlStateManager.disableLighting();
      }

      if (state.normal) {
         GlStateManager.enableNormalize();
      } else {
         GlStateManager.disableNormalize();
      }

      if (state.rescaleNormal) {
         GlStateManager.enableRescaleNormal();
      } else {
         GlStateManager.disableRescaleNormal();
      }

      if (state.depthTest) {
         GlStateManager.enableDepth();
      } else {
         GlStateManager.disableDepth();
      }

      if (state.texture2D) {
         GlStateManager.enableTexture2D();
      } else {
         GlStateManager.disableTexture2D();
      }

      GlStateManager.depthMask(state.depthMask);
   }
}
