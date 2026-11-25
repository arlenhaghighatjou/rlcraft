package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.SaveData;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExtTileTextureMap extends SaveData {
   private static final ExtTileTextureMap INSTANCE = new ExtTileTextureMap();
   final Map<String, TextureSet> textureMap = new HashMap();

   public ExtTileTextureMap() {
   }

   public static ExtTileTextureMap instance() {
      return INSTANCE;
   }

   public void setTexture(String tileName, TextureSet textureSet) {
      if (textureSet == null) {
         Log.error("Texture set is null!");
      } else {
         TextureSet previous = (TextureSet)this.textureMap.put(tileName, textureSet);
         if (previous == null) {
            this.markDirty();
         } else if (!previous.equals(textureSet)) {
            Log.warn("Overwriting texture set for tile \"%s\"", tileName);
            this.markDirty();
         }

      }
   }

   public TextureSet getTexture(String tileName) {
      TextureSet textureSet = (TextureSet)this.textureMap.get(tileName);
      return textureSet == null ? BiomeTextureMap.defaultTexture : textureSet;
   }

   public boolean isRegistered(String tileName) {
      return this.textureMap.containsKey(tileName);
   }
}
