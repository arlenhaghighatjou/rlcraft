package ivorius.ivtoolkit.bezier;

import ivorius.ivtoolkit.math.IvMathHelper;
import java.util.ArrayList;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;

public class IvBezierPath3DRendererText {
   private FontRenderer fontRenderer;
   private String text;
   private boolean spreadToFill;
   private double shift;
   private boolean inwards;
   private double capBottom;
   private double capTop = 1.0D;

   public IvBezierPath3DRendererText() {
   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   public void setFontRenderer(FontRenderer fontRenderer) {
      this.fontRenderer = fontRenderer;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public boolean isSpreadToFill() {
      return this.spreadToFill;
   }

   public void setSpreadToFill(boolean spreadToFill) {
      this.spreadToFill = spreadToFill;
   }

   public double getShift() {
      return this.shift;
   }

   public void setShift(double shift) {
      this.shift = shift;
   }

   public boolean isInwards() {
      return this.inwards;
   }

   public void setInwards(boolean inwards) {
      this.inwards = inwards;
   }

   public double getCapBottom() {
      return this.capBottom;
   }

   public void setCapBottom(double capBottom) {
      this.capBottom = capBottom;
   }

   public double getCapTop() {
      return this.capTop;
   }

   public void setCapTop(double capTop) {
      this.capTop = capTop;
   }

   public void render(IvBezierPath3D path) {
      if (path.isDirty()) {
         path.buildDistances();
      }

      GlStateManager.enableBlend();
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      String plainText = "";
      ArrayList<String> modifiers = new ArrayList();
      modifiers.add("");

      for(int i = 0; i < this.text.length(); ++i) {
         char character = this.text.charAt(i);
         if (character == 167 && i + 1 < this.text.length()) {
            modifiers.set(modifiers.size() - 1, (String)modifiers.get(modifiers.size() - 1) + this.text.substring(i, i + 2));
            ++i;
         } else {
            plainText = plainText + character;
            modifiers.add(modifiers.get(modifiers.size() - 1));
         }
      }

      for(int i = 0; i < plainText.length(); ++i) {
         int charIndex = this.inwards ? i : plainText.length() - i - 1;
         char character = plainText.charAt(charIndex);
         if (character != ' ') {
            double totalProgress = (this.spreadToFill ? (double)i / (double)this.text.length() : (double)i * 0.5D) + this.shift;
            double finalProgress = (totalProgress % 1.0D + 1.0D) % 1.0D;
            if (finalProgress >= this.capBottom && finalProgress <= this.capTop) {
               IvBezierPoint3DCachedStep cachedStep = path.getCachedStep(finalProgress);
               double[] position = cachedStep.getPosition();
               double[] rotation = path.getNaturalRotation(cachedStep, 0.01D);
               double red = IvMathHelper.mix(cachedStep.getLeftPoint().getRed(), cachedStep.getRightPoint().getRed(), cachedStep.getInnerProgress());
               double green = IvMathHelper.mix(cachedStep.getLeftPoint().getGreen(), cachedStep.getRightPoint().getGreen(), cachedStep.getInnerProgress());
               double blue = IvMathHelper.mix(cachedStep.getLeftPoint().getBlue(), cachedStep.getRightPoint().getBlue(), cachedStep.getInnerProgress());
               double alpha = IvMathHelper.mix(cachedStep.getLeftPoint().getAlpha(), cachedStep.getRightPoint().getAlpha(), cachedStep.getInnerProgress());
               double textSize = IvMathHelper.mix(cachedStep.getLeftPoint().getFontSize(), cachedStep.getRightPoint().getFontSize(), cachedStep.getInnerProgress());
               GlStateManager.pushMatrix();
               GlStateManager.translate(position[0], position[1], position[2]);
               GlStateManager.scale(-textSize / 12.0D, -textSize / 12.0D, -textSize / 12.0D);
               GlStateManager.rotate((float)rotation[0] + (this.inwards ? 0.0F : 180.0F), 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate((float)rotation[1], 1.0F, 0.0F, 0.0F);
               this.fontRenderer.drawString((String)modifiers.get(charIndex) + character, 0, 0, ((int)(red * 255.0D) << 16) + ((int)(green * 255.0D) << 8) + (int)(blue * 255.0D));
               GlStateManager.popMatrix();
            }
         }
      }

      GlStateManager.disableBlend();
   }
}
