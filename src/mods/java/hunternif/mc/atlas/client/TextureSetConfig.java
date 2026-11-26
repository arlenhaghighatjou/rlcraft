package hunternif.mc.atlas.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Map.Entry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureSetConfig extends AbstractJSONConfig<TextureSetMap> {
   private static final int VERSION = 1;

   public TextureSetConfig(VFile2 file) {
      super(file);
   }

   public int currentVersion() {
      return 1;
   }

   protected void loadData(JsonObject json, TextureSetMap data, int version) {
      for(Entry<String, JsonElement> entry : json.entrySet()) {
         String name = (String)entry.getKey();
         JsonArray array = ((JsonElement)entry.getValue()).getAsJsonArray();
         ResourceLocation[] textures = new ResourceLocation[array.size()];

         for(int i = 0; i < array.size(); ++i) {
            String path = array.get(i).getAsString();
            textures[i] = new ResourceLocation(path);
         }

         data.register(new TextureSet(name, textures));
         Log.info("Loaded texture set %s with %d custom texture(s)", name, textures.length);
      }

   }

   protected void saveData(JsonObject json, TextureSetMap data) {
      Queue<TextureSet> queue = new PriorityQueue(data.getAllNonStandardTextureSets());

      while(!queue.isEmpty()) {
         TextureSet set = (TextureSet)queue.poll();
         JsonArray paths = new JsonArray();

         for(ResourceLocation texture : set.textures) {
            paths.add(new JsonPrimitive(texture.toString()));
         }

         json.add(set.name, paths);
      }

   }
}
