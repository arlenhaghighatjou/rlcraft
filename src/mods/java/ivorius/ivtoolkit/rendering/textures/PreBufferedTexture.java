package ivorius.ivtoolkit.rendering.textures;

import java.awt.image.BufferedImage;
import java.io.IOException;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

public class PreBufferedTexture extends AbstractTexture {
   private BufferedImage bufferedImage;

   public PreBufferedTexture(BufferedImage bufferedImage) {
      this.bufferedImage = bufferedImage;
   }

   public BufferedImage getBufferedImage() {
      return this.bufferedImage;
   }

   public void loadTexture(IResourceManager var1) throws IOException {
      TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), this.bufferedImage, false, false);
   }
}
