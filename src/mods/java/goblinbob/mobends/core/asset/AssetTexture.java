package goblinbob.mobends.core.asset;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class AssetTexture extends AbstractTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   private final AssetLocation assetLocation;
   @Nullable
   private BufferedImage bufferedImage;
   private boolean textureUploaded = false;

   public AssetTexture(AssetLocation assetLocation) {
      this.assetLocation = assetLocation;
   }

   private void checkTextureUploaded() {
      if (!this.textureUploaded && this.bufferedImage != null) {
         TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
         this.textureUploaded = true;
      }

   }

   public int getGlTextureId() {
      this.checkTextureUploaded();
      return super.getGlTextureId();
   }

   public void loadTexture(IResourceManager resourceManager) throws IOException {
      try {
         this.bufferedImage = ImageIO.read(AssetsModule.INSTANCE.getAssetFile(this.assetLocation));
      } catch (IOException var3) {
         LOGGER.error("Couldn't load asset texture {}", this.assetLocation.toString(), var3);
         throw var3;
      }
   }
}
