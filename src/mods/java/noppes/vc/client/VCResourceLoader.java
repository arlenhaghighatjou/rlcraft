package noppes.vc.client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import noppes.vc.VariedCommodities;

public class VCResourceLoader implements IResourceManagerReloadListener {
   public VCResourceLoader() {
   }

   public void onResourceManagerReload(IResourceManager var1) {
      if (var1 instanceof SimpleReloadableResourceManager) {
         this.createTextureCache();
      }

   }

   private void createTextureCache() {
      this.enlargeTexture("planks_oak");
      this.enlargeTexture("planks_big_oak");
      this.enlargeTexture("planks_birch");
      this.enlargeTexture("planks_jungle");
      this.enlargeTexture("planks_spruce");
      this.enlargeTexture("planks_acacia");
      this.enlargeTexture("iron_block");
      this.enlargeTexture("diamond_block");
      this.enlargeTexture("stone");
      this.enlargeTexture("gold_block");
      this.enlargeTexture("wool_colored_white");
   }

   private void enlargeTexture(String texture) {
      TextureManager manager = Minecraft.getMinecraft().getTextureManager();
      if (manager != null) {
         ResourceLocation location = new ResourceLocation("variedcommodities", "textures/cache/" + texture + ".png");
         ITextureObject ob = manager.getTexture(location);
         if (ob == null || !(ob instanceof VCResourceLoader.TextureCache)) {
            ob = new VCResourceLoader.TextureCache(location);
            manager.loadTexture(location, ob);
         }

         ((VCResourceLoader.TextureCache)ob).setImage(new ResourceLocation("textures/blocks/" + texture + ".png"));
      }
   }

   class TextureCache extends SimpleTexture {
      private BufferedImage bufferedImage;
      private boolean textureUploaded;

      public TextureCache(ResourceLocation location) {
         super(location);
      }

      public int getGlTextureId() {
         this.checkTextureUploaded();
         return super.getGlTextureId();
      }

      private void checkTextureUploaded() {
         if (!this.textureUploaded && this.bufferedImage != null) {
            if (this.textureLocation != null && this.glTextureId != -1) {
               TextureUtil.deleteTexture(this.glTextureId);
               this.glTextureId = -1;
            }

            TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
            this.textureUploaded = true;
         }

      }

      public void setImage(ResourceLocation location) {
         try {
            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
            BufferedImage bufferedimage = ImageIO.read(manager.getResource(location).getInputStream());
            int i = bufferedimage.getWidth();
            int j = bufferedimage.getHeight();
            this.bufferedImage = new BufferedImage(i * 4, j * 2, 1);
            Graphics g = this.bufferedImage.getGraphics();
            g.drawImage(bufferedimage, 0, 0, (ImageObserver)null);
            g.drawImage(bufferedimage, i, 0, (ImageObserver)null);
            g.drawImage(bufferedimage, i * 2, 0, (ImageObserver)null);
            g.drawImage(bufferedimage, i * 3, 0, (ImageObserver)null);
            g.drawImage(bufferedimage, 0, i, (ImageObserver)null);
            g.drawImage(bufferedimage, i, j, (ImageObserver)null);
            g.drawImage(bufferedimage, i * 2, j, (ImageObserver)null);
            g.drawImage(bufferedimage, i * 3, j, (ImageObserver)null);
            this.textureUploaded = false;
         } catch (Exception var7) {
            VariedCommodities.Log.error("Failed caching texture: " + location, var7);
         }

      }

      public void loadTexture(IResourceManager resourceManager) throws IOException {
      }
   }
}
