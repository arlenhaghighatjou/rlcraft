package reborncore.client.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class FileSystemTexture extends AbstractTexture {
   protected final File textureLocation;
   BufferedImage image;

   public FileSystemTexture(File textureResourceLocation) {
      this.textureLocation = textureResourceLocation;
   }

   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      if (this.image == null) {
         IResource iresource = null;

         try {
            iresource = new IResource() {
               FileInputStream stream;

               public ResourceLocation getResourceLocation() {
                  return new ResourceLocation("reborncore:loaded/" + FileSystemTexture.this.textureLocation.getName());
               }

               public InputStream getInputStream() {
                  if (this.stream == null) {
                     try {
                        this.stream = new FileInputStream(FileSystemTexture.this.textureLocation);
                     } catch (FileNotFoundException var2) {
                        var2.printStackTrace();
                     }
                  }

                  return this.stream;
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
                  if (this.stream != null) {
                     this.stream.close();
                  }

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
