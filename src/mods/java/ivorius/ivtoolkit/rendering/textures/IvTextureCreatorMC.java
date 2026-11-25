package ivorius.ivtoolkit.rendering.textures;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Logger;

public class IvTextureCreatorMC {
   public IvTextureCreatorMC() {
   }

   public static BufferedImage getImage(IResourceManager resourceManager, ResourceLocation location, Logger logger) throws IOException {
      ITextureObject textureObject = Minecraft.getMinecraft().getTextureManager().getTexture(location);
      if (textureObject instanceof ThreadDownloadImageData) {
         return (BufferedImage)ReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, (ThreadDownloadImageData)textureObject, new String[]{"bufferedImage", "bufferedImage"});
      } else if (!(textureObject instanceof LayeredTexture)) {
         if (textureObject instanceof PreBufferedTexture) {
            return ((PreBufferedTexture)textureObject).getBufferedImage();
         } else {
            IResource iresource = resourceManager.getResource(location);
            InputStream inputstream = iresource.getInputStream();
            Throwable var38 = null;

            BufferedImage var39;
            try {
               var39 = ImageIO.read(inputstream);
            } catch (Throwable var33) {
               var38 = var33;
               throw var33;
            } finally {
               if (inputstream != null) {
                  if (var38 != null) {
                     try {
                        inputstream.close();
                     } catch (Throwable var30) {
                        var38.addSuppressed(var30);
                     }
                  } else {
                     inputstream.close();
                  }
               }

            }

            return var39;
         }
      } else {
         BufferedImage bufferedimage = null;

         for(Object layeredTextureNameObj : ((LayeredTexture)textureObject).layeredTextureNames) {
            String layeredTextureName = (String)layeredTextureNameObj;
            if (layeredTextureName != null) {
               InputStream inputstream = resourceManager.getResource(new ResourceLocation(layeredTextureName)).getInputStream();
               Throwable var9 = null;

               try {
                  BufferedImage bufferImageLayer = ImageIO.read(inputstream);
                  if (bufferedimage == null) {
                     bufferedimage = new BufferedImage(bufferImageLayer.getWidth(), bufferImageLayer.getHeight(), 2);
                  }

                  bufferedimage.getGraphics().drawImage(bufferImageLayer, 0, 0, (ImageObserver)null);
               } catch (Throwable var32) {
                  var9 = var32;
                  throw var32;
               } finally {
                  if (inputstream != null) {
                     if (var9 != null) {
                        try {
                           inputstream.close();
                        } catch (Throwable var31) {
                           var9.addSuppressed(var31);
                        }
                     } else {
                        inputstream.close();
                     }
                  }

               }
            }
         }

         return bufferedimage;
      }
   }

   public interface LoadingImageEffect extends IvTextureCreator.ImageEffect {
      void load(IResourceManager var1) throws IOException;
   }
}
