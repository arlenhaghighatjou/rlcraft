package ivorius.ivtoolkit.rendering.textures;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class IvTexturePatternColorizer implements IvTextureCreatorMC.LoadingImageEffect {
   private ResourceLocation resourceLocation;
   private BufferedImage bufferedImage;
   private Logger logger;
   private int[] patternColorComponents = new int[4];
   private float[] patternColor = new float[4];
   private float[] hsb = new float[4];

   public IvTexturePatternColorizer(ResourceLocation resourceLocation, Logger logger) {
      this.resourceLocation = resourceLocation;
      this.logger = logger;
   }

   public IvTexturePatternColorizer(BufferedImage bufferedImage, Logger logger) {
      this.bufferedImage = bufferedImage;
      this.logger = logger;
   }

   public void load(IResourceManager resourceManager) throws IOException {
      if (this.resourceLocation != null) {
         this.bufferedImage = IvTextureCreatorMC.getImage(resourceManager, this.resourceLocation, this.logger);
      }

   }

   public void getPixel(float[] color, float[] colorDest, int x, int y) {
      this.bufferedImage.getRaster().getPixel(x % this.bufferedImage.getWidth(), y % this.bufferedImage.getHeight(), this.patternColorComponents);
      this.hsb = Color.RGBtoHSB(this.patternColorComponents[0], this.patternColorComponents[1], this.patternColorComponents[2], (float[])null);
      this.hsb[2] = (0.2126F * color[0] / 255.0F + 0.7152F * color[1] / 255.0F + 0.0722F * color[2] / 255.0F) * 0.3F + this.hsb[2] * 0.7F;
      this.patternColor = IvColorHelper.getARBGFloats(Color.HSBtoRGB(this.hsb[0], this.hsb[1], this.hsb[2]));
      colorDest[0] = color[0] * 0.1F + this.patternColor[1] * 255.0F * 0.9F;
      colorDest[1] = color[1] * 0.1F + this.patternColor[2] * 255.0F * 0.9F;
      colorDest[2] = color[2] * 0.1F + this.patternColor[3] * 255.0F * 0.9F;
      colorDest[3] = color[3] * ((float)this.patternColorComponents[3] / 255.0F);
   }
}
