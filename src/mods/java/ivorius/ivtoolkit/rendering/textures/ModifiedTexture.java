package ivorius.ivtoolkit.rendering.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ModifiedTexture extends AbstractTexture {
   private ResourceLocation resourceLocation;
   private IvTextureCreatorMC.LoadingImageEffect imageEffect;
   private Logger logger;

   public ModifiedTexture(ResourceLocation resourceLocation, IvTextureCreatorMC.LoadingImageEffect imageEffect, Logger logger) {
      this.resourceLocation = resourceLocation;
      this.imageEffect = imageEffect;
      this.logger = logger;
   }

   public void loadTexture(IResourceManager var1) throws IOException {
      this.imageEffect.load(var1);
      BufferedImage image = IvTextureCreatorMC.getImage(var1, this.resourceLocation, this.logger);
      if (image == null) {
         throw new IOException();
      } else {
         BufferedImage modified = IvTextureCreator.applyEffect(image, this.imageEffect);
         TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), modified, false, false);
      }
   }
}
