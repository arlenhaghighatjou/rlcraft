package hunternif.mc.atlas.ext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import java.io.File;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Map.Entry;

public class ExtTileConfig extends AbstractJSONConfig<ExtTileIdMap> {
   private static final int VERSION = 1;

   public ExtTileConfig(File file) {
      super(file);
   }

   public int currentVersion() {
      return 1;
   }

   protected void loadData(JsonObject json, ExtTileIdMap data, int version) {
      for(Entry<String, JsonElement> entry : json.entrySet()) {
         String name = (String)entry.getKey();
         int id = ((JsonElement)entry.getValue()).getAsInt();
         data.setPseudoBiomeID(name, id);
      }

   }

   protected void saveData(JsonObject json, ExtTileIdMap data) {
      Queue<String> queue = new PriorityQueue(data.getMap().keySet());

      while(!queue.isEmpty()) {
         String tileName = (String)queue.poll();
         json.addProperty(tileName, Integer.valueOf(((Integer)data.getMap().get(tileName)).intValue()));
      }

   }
}
