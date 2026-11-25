package lumien.custommainmenu.lib.textures;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.handler.LoadTextureURL;
import lumien.custommainmenu.lib.StringReplacer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

public class TextureURL implements ITexture {
   URL url;
   int textureID = -1;
   private BufferedImage bi;

   public TextureURL(String url) {
      try {
         this.url = new URL(StringReplacer.replacePlaceholders(url));
      } catch (MalformedURLException var3) {
         CustomMainMenu.INSTANCE.logger.log(Level.ERROR, "Invalid URL: " + url);
         var3.printStackTrace();
      }

      (new LoadTextureURL(this)).start();
   }

   public void bind() {
      if (this.textureID != -1) {
         GlStateManager.bindTexture(this.textureID);
      } else {
         if (this.bi != null) {
            this.setTextureID(TextureUtil.uploadTextureImageAllocate(GL11.glGenTextures(), this.bi, false, false));
            this.bind();
            return;
         }

         CustomMainMenu.bindTransparent();
      }

   }

   public void finishLoading(BufferedImage bi) {
      this.bi = bi;
   }

   public URL getURL() {
      return this.url;
   }

   public void setTextureID(int textureID) {
      this.textureID = textureID;
   }
}
