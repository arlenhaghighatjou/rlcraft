package ivorius.ivtoolkit.bezier;

import ivorius.ivtoolkit.math.IvMathHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class IvBezierPath3DRendererTexture {
   private double lineWidth = 1.0D;
   private double stepSize = 0.1D;
   private double textureShift;

   public IvBezierPath3DRendererTexture() {
   }

   public double getLineWidth() {
      return this.lineWidth;
   }

   public void setLineWidth(double lineWidth) {
      this.lineWidth = lineWidth;
   }

   public double getStepSize() {
      return this.stepSize;
   }

   public void setStepSize(double stepSize) {
      this.stepSize = stepSize;
   }

   public double getTextureShift() {
      return this.textureShift;
   }

   public void setTextureShift(double textureShift) {
      this.textureShift = textureShift;
   }

   public void render(IvBezierPath3D path) {
      if (path.isDirty()) {
         path.buildDistances();
      }

      BufferBuilder renderer = Tessellator.getInstance().getBuffer();
      GlStateManager.enableBlend();
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);

      for(double progress = 0.0D; progress < 1.0D + this.stepSize; progress += this.stepSize) {
         boolean isVeryFirst = progress == 0.0D;
         boolean isVeryLast = progress >= 1.0D;
         double totalProgress = Math.min(progress, 1.0D);
         IvBezierPoint3DCachedStep cachedStep = path.getCachedStep(totalProgress);
         double[] position = cachedStep.getPosition();
         double[] pVector = path.getPVector(cachedStep, this.stepSize);
         double red = IvMathHelper.mix(cachedStep.getLeftPoint().getRed(), cachedStep.getRightPoint().getRed(), cachedStep.getInnerProgress());
         double green = IvMathHelper.mix(cachedStep.getLeftPoint().getGreen(), cachedStep.getRightPoint().getGreen(), cachedStep.getInnerProgress());
         double blue = IvMathHelper.mix(cachedStep.getLeftPoint().getBlue(), cachedStep.getRightPoint().getBlue(), cachedStep.getInnerProgress());
         double alpha = IvMathHelper.mix(cachedStep.getLeftPoint().getAlpha(), cachedStep.getRightPoint().getAlpha(), cachedStep.getInnerProgress());
         double textureX = totalProgress + this.textureShift;
         if (!isVeryFirst) {
            renderer.color((float)red, (float)green, (float)blue, (float)alpha);
            renderer.pos(position[0] - pVector[0] * this.lineWidth, position[1] - pVector[1] * this.lineWidth, position[2] - pVector[2] * this.lineWidth).tex(textureX, 0.0D).endVertex();
            renderer.pos(position[0] + pVector[0] * this.lineWidth, position[1] + pVector[1] * this.lineWidth, position[2] + pVector[2] * this.lineWidth).tex(textureX, 1.0D).endVertex();
            Tessellator.getInstance().draw();
         }

         if (!isVeryLast) {
            renderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            renderer.color((float)red, (float)green, (float)blue, (float)alpha);
            renderer.pos(position[0] + pVector[0] * this.lineWidth, position[1] + pVector[1] * this.lineWidth, position[2] + pVector[2] * this.lineWidth).tex(textureX, 1.0D).endVertex();
            renderer.pos(position[0] - pVector[0] * this.lineWidth, position[1] - pVector[1] * this.lineWidth, position[2] - pVector[2] * this.lineWidth).tex(textureX, 0.0D).endVertex();
         }
      }

      GlStateManager.disableBlend();
   }
}
