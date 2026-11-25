package lumien.custommainmenu.lib.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;

public class TextureResourceLocation extends ResourceLocation implements ITexture {
   public TextureResourceLocation(String resourceString) {
      super(resourceString);
   }

   public void bind() {
      Minecraft.getMinecraft().renderEngine.bindTexture(this);
   }

   public int getWidth() {
      ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(this);
      return texture instanceof SimpleTexture ? ((SimpleTexture)texture).getWidth() : 16;
   }

   public int getHeight() {
      ITextureObject texture = Minecraft.getMinecraft().getTextureManager().getTexture(this);
      return texture instanceof SimpleTexture ? ((SimpleTexture)texture).getHeight() : 16;
   }
}
