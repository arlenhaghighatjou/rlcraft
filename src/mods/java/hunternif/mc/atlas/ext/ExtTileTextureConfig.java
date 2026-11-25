package hunternif.mc.atlas.ext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.client.TextureSetMap;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;
import java.io.File;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Map.Entry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExtTileTextureConfig extends AbstractJSONConfig<ExtTileTextureMap> {
   private static final int VERSION = 1;
   private final TextureSetMap textureSetMap;

   public ExtTileTextureConfig(File file, TextureSetMap textureSetMap) {
      super(file);
      this.textureSetMap = textureSetMap;
   }

   public int currentVersion() {
      return 1;
   }

   protected void loadData(JsonObject json, ExtTileTextureMap data, int version) {
      for(Entry<String, JsonElement> entry : json.entrySet()) {
         String tileName = (String)entry.getKey();
         String textureName = ((JsonElement)entry.getValue()).getAsString();
         if (this.textureSetMap.isRegistered(textureName)) {
            data.setTexture(tileName, this.textureSetMap.getByName(textureName));
            Log.info("Registered texture set %s for tile \"%s\"", textureName, tileName);
         } else {
            Log.warn("Unknown texture set %s for tile \"%s\"", textureName, tileName);
         }
      }

   }

   protected void saveData(JsonObject json, ExtTileTextureMap data) {
      Queue<String> queue = new PriorityQueue(data.textureMap.keySet());

      while(!queue.isEmpty()) {
         String tileName = (String)queue.poll();
         json.addProperty(tileName, ((TextureSet)data.textureMap.get(tileName)).name);
      }

   }
}
