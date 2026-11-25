package ivorius.ivtoolkit.models.textures;

import ivorius.ivtoolkit.rendering.Icon;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

public class MinecraftIconProvider implements TextureProvider {
   private ResourceLocation texture;
   private Map<String, Icon> specialIconMap;
   private Icon defaultIcon;

   public MinecraftIconProvider(ResourceLocation texture, Map<String, Icon> specialIconMap, Icon defaultIcon) {
      this.texture = texture;
      this.specialIconMap = specialIconMap;
      this.defaultIcon = defaultIcon;
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public void setTexture(ResourceLocation texture) {
      this.texture = texture;
   }

   public Map<String, Icon> getSpecialIconMap() {
      return this.specialIconMap;
   }

   public void setSpecialIconMap(Map<String, Icon> specialIconMap) {
      this.specialIconMap = specialIconMap;
   }

   public Texture provideTexture(String textureName) {
      Icon specialIcon = (Icon)this.specialIconMap.get(textureName);
      Icon icon = specialIcon != null ? specialIcon : this.defaultIcon;
      Texture tex = new MinecraftTextureProvider.Texture(this.texture);
      return new TextureSub(tex, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV());
   }
}
