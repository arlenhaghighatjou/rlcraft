package reborncore.client.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class InputStreamTexture extends AbstractTexture {
   protected final InputStream textureLocation;
   BufferedImage image;
   String name;

   public InputStreamTexture(InputStream textureResourceLocation, String name) {
      this.textureLocation = textureResourceLocation;
      this.name = name;
   }

   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      if (this.image == null) {
         IResource iresource = null;

         try {
            iresource = new IResource() {
               public ResourceLocation getResourceLocation() {
                  return new ResourceLocation("reborncore:loaded/" + InputStreamTexture.this.name);
               }

               public InputStream getInputStream() {
                  return InputStreamTexture.this.textureLocation;
               }

               public boolean hasMetadata() {
                  return false;
               }

               public <T extends IMetadataSection> T getMetadata(String sectionName) {
                  return (T)null;
               }

               public String getResourcePackName() {
                  return "reborncore";
               }

               public void close() throws IOException {
               }
            };
            this.image = TextureUtil.readBufferedImage(iresource.getInputStream());
         } finally {
            IOUtils.closeQuietly(iresource);
         }
      }

      TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), this.image, false, false);
   }
}
