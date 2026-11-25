package lumien.custommainmenu.lib.textures;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.lib.StringReplacer;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.minecraft.client.renderer.texture.TextureUtil;

public class TextureURL implements ITexture {
   private ImageData pending;
   private int textureId = -1;
   private int width = 1;
   private int height = 1;

   public TextureURL(String url) {
      String location = StringReplacer.replacePlaceholders(url);
      if (location.startsWith("https://") || location.startsWith("http://")) {
         PlatformRuntime.downloadRemoteURIByteArray(location, bytes -> {
            if (bytes != null) {
               this.pending = ImageData.loadImageFile(bytes, "image/png");
            }
         });
      }
   }

   public void bind() {
      ImageData image = this.pending;
      if (image != null) {
         this.pending = null;
         this.textureId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), image, false, false);
         this.width = image.width;
         this.height = image.height;
      }
      if (this.textureId == -1) {
         CustomMainMenu.bindTransparent();
      } else {
         GlStateManager.bindTexture(this.textureId);
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }
}
