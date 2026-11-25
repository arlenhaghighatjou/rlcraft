package meldexun.entityculling.util.culling;

import java.nio.ByteBuffer;
import meldexun.renderlib.util.BufferUtil;
import meldexun.renderlib.util.GLUtil;
import meldexun.renderlib.util.UnsafeBuffer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL45;

public class GLHelper {
   private static final UnsafeBuffer<ByteBuffer> BYTE_BUFFER = new UnsafeBuffer(BufferUtil.allocate(4));
   private static boolean blend;
   private static int blendSrcFactor;
   private static int blendDstFactor;
   private static int blendSrcFactorAlpha;
   private static int blendDstFactorAlpha;
   private static boolean depthTest;
   private static int depthFunc;
   private static boolean depthMask;
   private static boolean cull;
   private static int cullFace;
   private static boolean colorMaskRed;
   private static boolean colorMaskGreen;
   private static boolean colorMaskBlue;
   private static boolean colorMaskAlpha;

   public GLHelper() {
   }

   public static void clearBufferSubData(int buffer, long offset, long size, int data) {
      BYTE_BUFFER.putInt(0L, data);
      if (GLUtil.CAPS.OpenGL45) {
         GL45.glClearNamedBufferSubData(buffer, 33334, offset, size, 36244, 5125, (ByteBuffer)BYTE_BUFFER.getBuffer());
      } else {
         GL15.glBindBuffer(34962, buffer);
         GL43.glClearBufferSubData(34962, 33334, offset, size, 36244, 5125, (ByteBuffer)BYTE_BUFFER.getBuffer());
         GL15.glBindBuffer(34962, 0);
      }

   }

   public static void copyBufferSubData(int readBuffer, int writeBuffer, long readOffset, long writeOffset, long size) {
      if (GLUtil.CAPS.OpenGL45) {
         GL45.glCopyNamedBufferSubData(readBuffer, writeBuffer, readOffset, writeOffset, size);
      } else {
         GL15.glBindBuffer(36662, readBuffer);
         GL15.glBindBuffer(36663, writeBuffer);
         GL31.glCopyBufferSubData(36662, 36663, readOffset, writeOffset, size);
         GL15.glBindBuffer(36662, 0);
         GL15.glBindBuffer(36663, 0);
      }

   }

   public static void saveShaderGLState() {
      blend = GlStateManager.blendState.blend.currentState;
      blendSrcFactor = GlStateManager.blendState.srcFactor;
      blendDstFactor = GlStateManager.blendState.dstFactor;
      blendSrcFactorAlpha = GlStateManager.blendState.srcFactorAlpha;
      blendDstFactorAlpha = GlStateManager.blendState.dstFactorAlpha;
      depthTest = GlStateManager.depthState.depthTest.currentState;
      depthFunc = GlStateManager.depthState.depthFunc;
      depthMask = GlStateManager.depthState.maskEnabled;
      cull = GlStateManager.cullState.cullFace.currentState;
      cullFace = GlStateManager.cullState.mode;
      colorMaskRed = GlStateManager.colorMaskState.red;
      colorMaskGreen = GlStateManager.colorMaskState.green;
      colorMaskBlue = GlStateManager.colorMaskState.blue;
      colorMaskAlpha = GlStateManager.colorMaskState.alpha;
   }

   public static void restoreShaderGLState() {
      if (blend) {
         GlStateManager.enableBlend();
      } else {
         GlStateManager.disableBlend();
      }

      GlStateManager.tryBlendFuncSeparate(blendSrcFactor, blendDstFactor, blendSrcFactorAlpha, blendDstFactorAlpha);
      if (depthTest) {
         GlStateManager.enableDepth();
      } else {
         GlStateManager.disableDepth();
      }

      GlStateManager.depthFunc(depthFunc);
      GlStateManager.depthMask(depthMask);
      if (cull) {
         GlStateManager.enableCull();
      } else {
         GlStateManager.disableCull();
      }

      GlStateManager.cullFace(cullFace);
      GlStateManager.colorMask(colorMaskRed, colorMaskGreen, colorMaskBlue, colorMaskAlpha);
   }
}
