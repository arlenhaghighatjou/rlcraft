package ivorius.ivtoolkit.rendering;

import org.apache.logging.log4j.Logger;

public abstract class IvShaderInstance2D extends IvShaderInstance implements Iv2DScreenEffect {
   public IvShaderInstance2D(Logger logger) {
      super(logger);
   }

   public boolean shouldApply(float ticks) {
      return this.getShaderID() > 0;
   }

   public abstract void apply(int var1, int var2, float var3, IvOpenGLTexturePingPong var4);

   public void drawFullScreen(int screenWidth, int screenHeight, IvOpenGLTexturePingPong pingPong) {
      pingPong.pingPong();
      IvRenderHelper.drawRectFullScreen(screenWidth, screenHeight);
   }

   public void destruct() {
      this.deleteShader();
   }
}
