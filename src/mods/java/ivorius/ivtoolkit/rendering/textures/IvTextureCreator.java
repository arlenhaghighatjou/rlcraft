package ivorius.ivtoolkit.rendering.textures;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;

public class IvTextureCreator {
   public IvTextureCreator() {
   }

   public static BufferedImage applyEffect(BufferedImage texture, IvTextureCreator.ImageEffect effect) {
      if (texture.getType() == 13) {
         BufferedImage notIndexed = new BufferedImage(texture.getWidth(), texture.getHeight(), 2);
         notIndexed.getGraphics().drawImage(texture, 0, 0, (ImageObserver)null);
         texture = notIndexed;
      }

      BufferedImage modified = new BufferedImage(texture.getWidth(), texture.getHeight(), 2);
      WritableRaster sourceRaster = texture.getRaster();
      WritableRaster destRaster = modified.getRaster();
      float[] normalizedPixelColors = new float[4];
      float[] normalizedPixelColorsDest = new float[4];

      for(int x = 0; x < texture.getWidth(); ++x) {
         for(int y = 0; y < texture.getHeight(); ++y) {
            sourceRaster.getPixel(x, y, normalizedPixelColors);
            effect.getPixel(normalizedPixelColors, normalizedPixelColorsDest, x, y);
            destRaster.setPixel(x, y, normalizedPixelColorsDest);
         }
      }

      return modified;
   }

   public interface ImageEffect {
      void getPixel(float[] var1, float[] var2, int var3, int var4);
   }
}
